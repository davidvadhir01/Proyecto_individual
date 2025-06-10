package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Página principal - Dashboard
     */
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
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
            
            return "home";
            
        } catch (Exception e) {
            System.err.println("Error al cargar home: " + e.getMessage());
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("isAdmin", false);
            return "home";
        }
    }

    /**
     * Dashboard administrativo
     */
    @GetMapping("/admin")
    public String adminDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // Verificar si es admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            return "redirect:/home";
        }
        
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername()).orElse(null);
            model.addAttribute("usuario", userDetails.getUsername());
            model.addAttribute("usuarioCompleto", usuario);
            model.addAttribute("isAdmin", true);
            
            return "admin";
            
        } catch (Exception e) {
            System.err.println("Error al cargar panel admin: " + e.getMessage());
            model.addAttribute("usuario", userDetails.getUsername());
            model.addAttribute("isAdmin", true);
            return "admin";
        }
    }

    // ✅ SOLO estos 2 métodos - eliminé buscarPeliculas() y buscarLibros()
    // ✅ El SearchController se encarga de las búsquedas
}