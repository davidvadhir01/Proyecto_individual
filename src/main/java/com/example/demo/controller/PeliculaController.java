package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.*;

@Controller
public class PeliculaController {

    @Value("${tmdb.api.key:34bc0b489d3b104e0460cdf45b3b5f2c}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    private final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    // ========== TU MÉTODO EXISTENTE PARA PÁGINA WEB ==========
    
    @GetMapping("/peliculas")
    public String peliculas(Model model) {
        return "peliculas";
    }

    // ========== MÉTODOS API NUEVOS (AGREGADOS) ==========

    /**
     * API: Buscar películas usando TMDB API
     */
    @GetMapping("/peliculas/api/buscar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarPeliculasApi(@RequestParam String query) {
        try {
            if (tmdbApiKey == null || tmdbApiKey.trim().isEmpty()) {
                // Retornar datos de ejemplo si no hay API key
                return ResponseEntity.ok(crearDatosMockPeliculas(query));
            }

            String url = String.format(
                "%s/search/movie?api_key=%s&query=%s&language=es-ES&page=1",
                TMDB_BASE_URL, tmdbApiKey, query
            );

            System.out.println("Buscando películas con query: " + query);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                List<Map<String, Object>> peliculasFormateadas = new ArrayList<>();

                for (Map<String, Object> movie : results) {
                    Map<String, Object> pelicula = new HashMap<>();
                    pelicula.put("id", movie.get("id"));
                    pelicula.put("titulo", movie.get("title"));
                    pelicula.put("sinopsis", movie.get("overview"));
                    pelicula.put("fechaEstreno", movie.get("release_date"));
                    pelicula.put("calificacion", movie.get("vote_average"));
                    pelicula.put("popularidad", movie.get("popularity"));
                    pelicula.put("idioma", movie.get("original_language"));
                    
                    // Imagen de poster
                    String posterPath = (String) movie.get("poster_path");
                    if (posterPath != null) {
                        pelicula.put("imagen", TMDB_IMAGE_BASE_URL + posterPath);
                    } else {
                        pelicula.put("imagen", "https://via.placeholder.com/500x750?text=Sin+Imagen");
                    }
                    
                    peliculasFormateadas.add(pelicula);
                }

                Map<String, Object> resultado = new HashMap<>();
                resultado.put("peliculas", peliculasFormateadas);
                resultado.put("total", response.get("total_results"));
                resultado.put("fuente", "TMDB API");
                
                return ResponseEntity.ok(resultado);
            }

            return ResponseEntity.ok(crearDatosMockPeliculas(query));

        } catch (RestClientException e) {
            System.err.println("Error al buscar películas: " + e.getMessage());
            return ResponseEntity.ok(crearDatosMockPeliculas(query));
        } catch (Exception e) {
            System.err.println("Error general en búsqueda de películas: " + e.getMessage());
            return ResponseEntity.ok(crearDatosMockPeliculas(query));
        }
    }

    /**
     * API: Obtener películas populares
     */
    @GetMapping("/peliculas/api/populares")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> peliculasPopulares() {
        try {
            if (tmdbApiKey == null || tmdbApiKey.trim().isEmpty()) {
                return ResponseEntity.ok(crearDatosMockPeliculas("populares"));
            }

            String url = String.format(
                "%s/movie/popular?api_key=%s&language=es-ES&page=1",
                TMDB_BASE_URL, tmdbApiKey
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                List<Map<String, Object>> peliculasFormateadas = new ArrayList<>();

                for (Map<String, Object> movie : results) {
                    Map<String, Object> pelicula = new HashMap<>();
                    pelicula.put("id", movie.get("id"));
                    pelicula.put("titulo", movie.get("title"));
                    pelicula.put("sinopsis", movie.get("overview"));
                    pelicula.put("fechaEstreno", movie.get("release_date"));
                    pelicula.put("calificacion", movie.get("vote_average"));
                    
                    String posterPath = (String) movie.get("poster_path");
                    if (posterPath != null) {
                        pelicula.put("imagen", TMDB_IMAGE_BASE_URL + posterPath);
                    } else {
                        pelicula.put("imagen", "https://via.placeholder.com/500x750?text=Sin+Imagen");
                    }
                    
                    peliculasFormateadas.add(pelicula);
                }

                Map<String, Object> resultado = new HashMap<>();
                resultado.put("peliculas", peliculasFormateadas);
                resultado.put("total", response.get("total_results"));
                resultado.put("fuente", "TMDB API");
                
                return ResponseEntity.ok(resultado);
            }

            return ResponseEntity.ok(crearDatosMockPeliculas("populares"));

        } catch (Exception e) {
            System.err.println("Error al obtener películas populares: " + e.getMessage());
            return ResponseEntity.ok(crearDatosMockPeliculas("populares"));
        }
    }

    /**
     * API: Obtener mis películas guardadas
     */
    @GetMapping("/peliculas/api/mis-peliculas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerMisPeliculas() {
        try {
            // Por ahora retornamos una lista vacía ya que no tienes servicio de base de datos
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("peliculas", new ArrayList<>());
            resultado.put("total", 0);
            resultado.put("mensaje", "Funcionalidad de base de datos no implementada aún");
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            System.err.println("Error al obtener mis películas: " + e.getMessage());
            return ResponseEntity.ok(new HashMap<>());
        }
    }

    /**
     * API: Agregar película a favoritos/base de datos
     */
    @PostMapping("/peliculas/api/agregar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarPelicula(@RequestBody Map<String, Object> peliculaData) {
        try {
            // Por ahora solo confirmamos que se recibieron los datos
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Película agregada a favoritos (funcionalidad completa pendiente)");
            respuesta.put("titulo", peliculaData.get("titulo"));
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            System.err.println("Error al agregar película: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al agregar película: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Datos de ejemplo cuando no funciona la API
     */
    private Map<String, Object> crearDatosMockPeliculas(String query) {
        List<Map<String, Object>> peliculasMock = Arrays.asList(
            crearPeliculaMock("1", "El Padrino", "La historia de una familia mafiosa italiana en Nueva York...", "1972-03-24", 9.2,
                "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg"),
            crearPeliculaMock("2", "Pulp Fiction", "Historias entrelazadas de crimen en Los Ángeles...", "1994-10-14", 8.9,
                "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg"),
            crearPeliculaMock("3", "El Señor de los Anillos", "Un hobbit debe destruir un anillo mágico...", "2001-12-19", 8.8,
                "https://image.tmdb.org/t/p/w500/6oom5QYQ2yQTMJIbnvbkBL9cHo6.jpg"),
            crearPeliculaMock("4", "Matrix", "Un programador descubre la verdad sobre la realidad...", "1999-03-31", 8.7,
                "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg"),
            crearPeliculaMock("5", "Forrest Gump", "La vida extraordinaria de un hombre simple...", "1994-07-06", 8.8,
                "https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg"),
            crearPeliculaMock("6", "Avengers: Endgame", "Los superhéroes se unen para enfrentar a Thanos...", "2019-04-26", 8.4,
                "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg")
        );

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("peliculas", peliculasMock);
        resultado.put("total", peliculasMock.size());
        resultado.put("fuente", "Datos de ejemplo (configura tu API key)");
        resultado.put("mensaje", "Para obtener datos reales, configura TMDB_API_KEY en application.properties");
        
        return resultado;
    }

    private Map<String, Object> crearPeliculaMock(String id, String titulo, String sinopsis, 
                                                  String fechaEstreno, double calificacion, String imagen) {
        Map<String, Object> pelicula = new HashMap<>();
        pelicula.put("id", id);
        pelicula.put("titulo", titulo);
        pelicula.put("sinopsis", sinopsis);
        pelicula.put("fechaEstreno", fechaEstreno);
        pelicula.put("calificacion", calificacion);
        pelicula.put("popularidad", 85.5);
        pelicula.put("idioma", "es");
        pelicula.put("imagen", imagen);
        return pelicula;
    }
}