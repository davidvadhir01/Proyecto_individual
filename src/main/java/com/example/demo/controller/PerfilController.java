package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Tipos de archivo permitidos para imágenes
    private static final List<String> TIPOS_IMAGEN_PERMITIDOS = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // Tamaño máximo de archivo (5MB)
    private static final long TAMAÑO_MAXIMO_ARCHIVO = 5 * 1024 * 1024;

    public PerfilController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/perfil/editar")
    public String mostrarFormularioEdicion(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("isAdmin", isAdmin);
            
            return "editar_perfil";
        } catch (Exception e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            return "redirect:/error";
        }
    }

    @PostMapping("/perfil/editar")
    public String actualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String peliculaFavorita,
            @RequestParam(required = false) String libroFavorito,
            @RequestParam(required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar datos básicos
            usuario.setNombre(nombre);
            usuario.setEmail(email);

            // Actualizar contraseña si se proporcionó
            if (password != null && !password.trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(password));
            }

            // Actualizar favoritos
            usuario.setPeliculaFavorita(peliculaFavorita);
            usuario.setLibroFavorito(libroFavorito);

            // Procesar imagen de perfil si se subió
            if (imagen != null && !imagen.isEmpty()) {
                String resultadoImagen = procesarImagenPerfil(imagen, usuario);
                if (!resultadoImagen.equals("OK")) {
                    redirectAttributes.addFlashAttribute("error", resultadoImagen);
                    return "redirect:/perfil/editar";
                }
            }

            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
            
            // Determinar redirección según el rol
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            
            return isAdmin ? "redirect:/admin" : "redirect:/home";
            
        } catch (Exception e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }

    /**
     * Procesa y valida la imagen de perfil subida
     */
    private String procesarImagenPerfil(MultipartFile archivo, Usuario usuario) {
        try {
            // Validar que no esté vacío
            if (archivo.isEmpty()) {
                return "El archivo de imagen está vacío";
            }

            // Validar tamaño
            if (archivo.getSize() > TAMAÑO_MAXIMO_ARCHIVO) {
                return "El archivo es demasiado grande. Máximo permitido: 5MB";
            }

            // Validar tipo de archivo
            String tipoContenido = archivo.getContentType();
            if (tipoContenido == null || !TIPOS_IMAGEN_PERMITIDOS.contains(tipoContenido.toLowerCase())) {
                return "Tipo de archivo no permitido. Solo se permiten: JPEG, PNG, GIF, WebP";
            }

            // Validar nombre del archivo
            String nombreArchivo = archivo.getOriginalFilename();
            if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
                nombreArchivo = "imagen_perfil." + obtenerExtensionDeTipo(tipoContenido);
            }

            // Convertir a bytes y guardar en el usuario
            byte[] bytesImagen = archivo.getBytes();
            usuario.setImagen(bytesImagen);
            usuario.setImagenNombre(nombreArchivo);
            usuario.setImagenTipo(tipoContenido);

            System.out.println("Imagen procesada exitosamente: " + nombreArchivo + 
                             " (" + bytesImagen.length + " bytes)");

            return "OK";

        } catch (IOException e) {
            System.err.println("Error al procesar imagen: " + e.getMessage());
            return "Error al procesar la imagen: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Error inesperado al procesar imagen: " + e.getMessage());
            return "Error inesperado al procesar la imagen";
        }
    }

    /**
     * Obtiene la extensión de archivo basada en el tipo MIME
     */
    private String obtenerExtensionDeTipo(String tipoMime) {
        switch (tipoMime.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "jpg";
        }
    }
}