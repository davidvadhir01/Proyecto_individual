package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.MovieService;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private MovieService movieService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Búsqueda de películas - USA TU PLANTILLA buscar.html
     */
    @GetMapping("/buscar/peliculas")
    public String buscarPeliculas(
            @RequestParam(required = false) String query, 
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        // Verificar autenticación
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // Cargar datos del usuario para mantener sesión
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername()).orElse(null);
            model.addAttribute("usuario", usuario);
            model.addAttribute("username", userDetails.getUsername());
            
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } catch (Exception e) {
            System.err.println("Error cargando usuario: " + e.getMessage());
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("isAdmin", false);
        }
        
        // Configurar para películas
        model.addAttribute("searchType", "peliculas");
        model.addAttribute("pageTitle", "Buscar Películas");
        model.addAttribute("searchPlaceholder", "Buscar películas...");
        
        // Realizar búsqueda si hay query
        if (query != null && !query.trim().isEmpty()) {
            try {
                List<Map<String, Object>> peliculas = movieService.buscarPeliculas(query);
                model.addAttribute("resultados", peliculas);
                model.addAttribute("query", query);
                
                if (peliculas.isEmpty()) {
                    model.addAttribute("mensaje", "No se encontraron películas para: " + query);
                } else {
                    model.addAttribute("mensaje", "Se encontraron " + peliculas.size() + " películas");
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error al buscar películas: " + e.getMessage());
                System.err.println("Error en búsqueda de películas: " + e.getMessage());
            }
        }
        
        return "buscar"; // TU plantilla existente
    }

    /**
     * Búsqueda de libros - USA TU PLANTILLA buscar.html
     */
    @GetMapping("/buscar/libros")
    public String buscarLibros(
            @RequestParam(required = false) String query, 
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        // Verificar autenticación
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // Cargar datos del usuario para mantener sesión
        try {
            Usuario usuario = usuarioRepository.findByNombre(userDetails.getUsername()).orElse(null);
            model.addAttribute("usuario", usuario);
            model.addAttribute("username", userDetails.getUsername());
            
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } catch (Exception e) {
            System.err.println("Error cargando usuario: " + e.getMessage());
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("isAdmin", false);
        }
        
        // Configurar para libros
        model.addAttribute("searchType", "libros");
        model.addAttribute("pageTitle", "Buscar Libros");
        model.addAttribute("searchPlaceholder", "Buscar libros...");
        
        // Realizar búsqueda si hay query
        if (query != null && !query.trim().isEmpty()) {
            try {
                List<Map<String, Object>> libros = bookService.buscarLibros(query);
                model.addAttribute("resultados", libros);
                model.addAttribute("query", query);
                
                if (libros.isEmpty()) {
                    model.addAttribute("mensaje", "No se encontraron libros para: " + query);
                } else {
                    model.addAttribute("mensaje", "Se encontraron " + libros.size() + " libros");
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error al buscar libros: " + e.getMessage());
                System.err.println("Error en búsqueda de libros: " + e.getMessage());
            }
        }
        
        return "buscar"; // TU plantilla existente
    }
    
    /**
     * API endpoints para AJAX
     */
    @GetMapping("/api/search/peliculas")
    @ResponseBody
    public Map<String, Object> buscarPeliculasApi(@RequestParam String query) {
        try {
            List<Map<String, Object>> peliculas = movieService.buscarPeliculas(query);
            return Map.of(
                "success", true,
                "data", peliculas,
                "total", peliculas.size()
            );
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
    
    @GetMapping("/api/search/libros")
    @ResponseBody
    public Map<String, Object> buscarLibrosApi(@RequestParam String query) {
        try {
            List<Map<String, Object>> libros = bookService.buscarLibros(query);
            return Map.of(
                "success", true,
                "data", libros,
                "total", libros.size()
            );
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}