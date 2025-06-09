package com.example.demo.controller;

import com.example.demo.service.MovieService;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/buscar")
public class SearchController {

    @Autowired
    private MovieService movieService;
    
    @Autowired
    private BookService bookService;

    /**
     * ✅ CORREGIDO: Página de búsqueda de películas
     */
    @GetMapping("/peliculas")
    public String buscarPeliculas(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("searchType", "peliculas");
        model.addAttribute("pageTitle", "Buscar Películas");
        model.addAttribute("searchPlaceholder", "Buscar películas... (ej: Avengers, Batman)");
        
        if (query != null && !query.trim().isEmpty()) {
            try {
                List<Map<String, Object>> peliculas = movieService.buscarPeliculas(query);
                model.addAttribute("resultados", peliculas);
                model.addAttribute("query", query);
                model.addAttribute("totalResultados", peliculas.size());
                
                if (peliculas.isEmpty()) {
                    model.addAttribute("mensaje", "No se encontraron películas para: " + query);
                } else {
                    model.addAttribute("mensaje", "Se encontraron " + peliculas.size() + " películas para: " + query);
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error al buscar películas: " + e.getMessage());
                System.err.println("Error en búsqueda de películas: " + e.getMessage());
            }
        }
        
        return "buscar";
    }

    /**
     * ✅ CORREGIDO: Página de búsqueda de libros
     */
    @GetMapping("/libros")
    public String buscarLibros(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("searchType", "libros");
        model.addAttribute("pageTitle", "Buscar Libros");
        model.addAttribute("searchPlaceholder", "Buscar libros... (ej: Harry Potter, Cien años)");
        
        if (query != null && !query.trim().isEmpty()) {
            try {
                List<Map<String, Object>> libros = bookService.buscarLibros(query);
                model.addAttribute("resultados", libros);
                model.addAttribute("query", query);
                model.addAttribute("totalResultados", libros.size());
                
                if (libros.isEmpty()) {
                    model.addAttribute("mensaje", "No se encontraron libros para: " + query);
                } else {
                    model.addAttribute("mensaje", "Se encontraron " + libros.size() + " libros para: " + query);
                }
            } catch (Exception e) {
                model.addAttribute("error", "Error al buscar libros: " + e.getMessage());
                System.err.println("Error en búsqueda de libros: " + e.getMessage());
            }
        }
        
        return "buscar";
    }
    
    /**
     * API endpoint para búsqueda de películas (AJAX)
     */
    @GetMapping("/api/peliculas")
    @ResponseBody
    public Map<String, Object> buscarPeliculasApi(@RequestParam String query) {
        try {
            List<Map<String, Object>> peliculas = movieService.buscarPeliculas(query);
            return Map.of(
                "success", true,
                "data", peliculas,
                "total", peliculas.size(),
                "query", query,
                "type", "peliculas"
            );
        } catch (Exception e) {
            System.err.println("Error en API de películas: " + e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "query", query,
                "type", "peliculas"
            );
        }
    }
    
    /**
     * API endpoint para búsqueda de libros (AJAX)
     */
    @GetMapping("/api/libros")
    @ResponseBody
    public Map<String, Object> buscarLibrosApi(@RequestParam String query) {
        try {
            List<Map<String, Object>> libros = bookService.buscarLibros(query);
            return Map.of(
                "success", true,
                "data", libros,
                "total", libros.size(),
                "query", query,
                "type", "libros"
            );
        } catch (Exception e) {
            System.err.println("Error en API de libros: " + e.getMessage());
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "query", query,
                "type", "libros"
            );
        }
    }
    
    /**
     * Obtener detalles de una película específica
     */
    @GetMapping("/pelicula/{id}")
    @ResponseBody
    public Map<String, Object> obtenerDetallesPelicula(@PathVariable String id) {
        try {
            Map<String, Object> detalles = movieService.obtenerDetallesPelicula(id);
            return Map.of("success", true, "data", detalles, "type", "pelicula");
        } catch (Exception e) {
            System.err.println("Error obteniendo detalles de película: " + e.getMessage());
            return Map.of("success", false, "error", e.getMessage(), "type", "pelicula");
        }
    }
    
    /**
     * Obtener detalles de un libro específico
     */
    @GetMapping("/libro/{id}")
    @ResponseBody
    public Map<String, Object> obtenerDetallesLibro(@PathVariable String id) {
        try {
            Map<String, Object> detalles = bookService.obtenerDetallesLibro(id);
            return Map.of("success", true, "data", detalles, "type", "libro");
        } catch (Exception e) {
            System.err.println("Error obteniendo detalles de libro: " + e.getMessage());
            return Map.of("success", false, "error", e.getMessage(), "type", "libro");
        }
    }
}