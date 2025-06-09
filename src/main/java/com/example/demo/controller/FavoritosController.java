package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class FavoritosController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Página de favoritos del usuario
     */
    @GetMapping("/mis-favoritos")
    public String misFavoritos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("username", userDetails.getUsername());
            
            // Verificar si es admin
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
            
            return "favoritos";
            
        } catch (Exception e) {
            System.err.println("Error al buscar favoritos: " + e.getMessage());
            return "redirect:/error";
        }
    }
    
    /**
     * API para actualizar película favorita
     */
    @PostMapping("/api/favoritos/pelicula")
    @ResponseBody
    public Map<String, Object> actualizarPeliculaFavorita(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            String peliculaFavorita = request.get("titulo");
            usuario.setPeliculaFavorita(peliculaFavorita);
            usuarioRepository.save(usuario);
            
            return Map.of(
                "success", true,
                "mensaje", "Película favorita actualizada",
                "pelicula", peliculaFavorita
            );
            
        } catch (Exception e) {
            System.err.println("Error al actualizar película favorita: " + e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
    
    /**
     * API para actualizar libro favorito
     */
    @PostMapping("/api/favoritos/libro")
    @ResponseBody
    public Map<String, Object> actualizarLibroFavorito(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            String libroFavorito = request.get("titulo");
            usuario.setLibroFavorito(libroFavorito);
            usuarioRepository.save(usuario);
            
            return Map.of(
                "success", true,
                "mensaje", "Libro favorito actualizado",
                "libro", libroFavorito
            );
            
        } catch (Exception e) {
            System.err.println("Error al actualizar libro favorito: " + e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
    
    /**
     * API para obtener favoritos del usuario
     */
    @GetMapping("/api/favoritos")
    @ResponseBody
    public Map<String, Object> obtenerFavoritos(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            return Map.of(
                "success", true,
                "favoritos", Map.of(
                    "pelicula", usuario.getPeliculaFavorita() != null ? usuario.getPeliculaFavorita() : "",
                    "libro", usuario.getLibroFavorito() != null ? usuario.getLibroFavorito() : ""
                )
            );
            
        } catch (Exception e) {
            System.err.println("Error al obtener favoritos: " + e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
}