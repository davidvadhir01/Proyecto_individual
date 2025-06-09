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
     * Página principal - Dashboard (RUTA ORIGINAL /)
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
     * Página de búsqueda de películas
     */
    @GetMapping("/buscar/peliculas")
    public String buscarPeliculas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("pageTitle", "Buscar Películas");
        
        return "buscar-peliculas";
    }

    /**
     * Página de búsqueda de libros
     */
    @GetMapping("/buscar/libros")
    public String buscarLibros(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("pageTitle", "Buscar Libros");
        
        return "buscar-libros";
    }

    /**
     * Página de favoritos
     */
    @GetMapping("/mis-favoritos")
    public String misFavoritos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("pageTitle", "Mis Favoritos");
        
        return "favoritos";
    }

    /**
     * Página de editar perfil (RESTAURADA)
     */
    @GetMapping("/editar-perfil")
    public String editarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("username", userDetails.getUsername());
            
            return "editar-perfil";
            
        } catch (Exception e) {
            System.err.println("Error al cargar perfil: " + e.getMessage());
            return "redirect:/home";
        }
    }

    /**
     * Dashboard administrativo (MANTENIDO)
     */
    @GetMapping("/admin-dashboard")
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
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("isAdmin", true);
        
        return "admin-dashboard";
    }
}