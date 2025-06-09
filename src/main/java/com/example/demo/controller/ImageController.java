package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Endpoint para servir las imágenes de perfil de usuarios
     * URL: /api/usuarios/{id}/imagen
     */
    @GetMapping("/usuarios/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagenUsuario(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Verificar si el usuario tiene imagen
            if (usuario.getImagen() == null || usuario.getImagen().length == 0) {
                return ResponseEntity.notFound().build();
            }
            
            // Determinar el tipo de contenido
            String contentType = usuario.getImagenTipo();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "image/jpeg"; // Tipo por defecto
            }
            
            // Configurar las cabeceras de respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(usuario.getImagen().length);
            
            // Opcional: agregar cache headers para mejorar performance
            headers.setCacheControl("public, max-age=31536000"); // Cache por 1 año
            
            return new ResponseEntity<>(usuario.getImagen(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Error al servir imagen para usuario " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint para obtener información básica sobre la imagen
     */
    @GetMapping("/usuarios/{id}/imagen/info")
    public ResponseEntity<?> obtenerInfoImagenUsuario(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Usuario usuario = usuarioOpt.get();
            
            if (!usuario.tieneImagen()) {
                return ResponseEntity.ok().body("{\"hasImage\": false}");
            }
            
            String info = String.format(
                "{\"hasImage\": true, \"fileName\": \"%s\", \"contentType\": \"%s\", \"size\": %d}",
                usuario.getImagenNombre() != null ? usuario.getImagenNombre() : "unknown",
                usuario.getImagenTipo() != null ? usuario.getImagenTipo() : "image/jpeg",
                usuario.getImagen().length
            );
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(info);
                
        } catch (Exception e) {
            System.err.println("Error al obtener info de imagen para usuario " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Error interno del servidor\"}");
        }
    }
}