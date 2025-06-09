package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        // Pasar el nombre de usuario al modelo
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            
            // Buscar y pasar los datos completos del usuario
            try {
                Usuario usuario = usuarioRepository.findByNombre(authentication.getName())
                    .orElse(null);
                    
                if (usuario != null) {
                    model.addAttribute("usuario", usuario);
                    System.out.println("Usuario encontrado: " + usuario.getNombre() + 
                                     " - Tiene imagen: " + usuario.tieneImagen());
                } else {
                    System.out.println("Usuario no encontrado en la base de datos: " + authentication.getName());
                }
            } catch (Exception e) {
                System.err.println("Error al buscar usuario: " + e.getMessage());
            }
            
            // Verificar si es admin para redirigir
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin";
            }
        }
        return "home";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminPanel(Model model, Authentication authentication) {
        model.addAttribute("usuario", authentication.getName());
        
        // Buscar y pasar los datos completos del usuario admin
        try {
            Usuario usuario = usuarioRepository.findByNombre(authentication.getName())
                .orElse(null);
                
            if (usuario != null) {
                model.addAttribute("usuarioCompleto", usuario);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar usuario admin: " + e.getMessage());
        }
        
        return "admin";
    }

    @GetMapping("/v1/home")
    public String homeV1() {
        return "redirect:/home";
    }
}