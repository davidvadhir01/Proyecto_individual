package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class MovieService {
    
    @Value("${tmdb.api.key:tu_api_key_aqui}")
    private String apiKey;
    
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Buscar películas por término de búsqueda
     */
    public List<Map<String, Object>> buscarPeliculas(String query) {
        try {
            String url = UriComponentsBuilder.fromUriString(BASE_URL + "/search/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("query", query)
                    .queryParam("language", "es-ES")
                    .queryParam("page", 1)
                    .build()
                    .toString();
            
            System.out.println("Buscando películas...");
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                
                List<Map<String, Object>> peliculas = new ArrayList<>();
                
                for (Map<String, Object> pelicula : results) {
                    if (peliculas.size() >= 20) break; // Limitar a 20 resultados
                    
                    Map<String, Object> peliculaProcesada = new HashMap<>();
                    peliculaProcesada.put("id", pelicula.get("id"));
                    peliculaProcesada.put("titulo", pelicula.get("title"));
                    
                    // Procesar descripción
                    String overview = (String) pelicula.get("overview");
                    if (overview != null && overview.length() > 200) {
                        overview = overview.substring(0, 200) + "...";
                    }
                    peliculaProcesada.put("sinopsis", overview);
                    
                    peliculaProcesada.put("fechaEstreno", pelicula.get("release_date"));
                    peliculaProcesada.put("puntuacion", pelicula.get("vote_average"));
                    
                    // Procesar imagen
                    String posterPath = (String) pelicula.get("poster_path");
                    if (posterPath != null && !posterPath.isEmpty()) {
                        peliculaProcesada.put("imagen", IMAGE_BASE_URL + posterPath);
                    } else {
                        peliculaProcesada.put("imagen", "/images/no-movie-poster.png");
                    }
                    
                    peliculaProcesada.put("tipo", "pelicula");
                    
                    peliculas.add(peliculaProcesada);
                }
                
                return peliculas;
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("Error al buscar películas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener detalles de una película específica
     */
    public Map<String, Object> obtenerDetallesPelicula(String movieId) {
        try {
            String url = UriComponentsBuilder.fromUriString(BASE_URL + "/movie/" + movieId)
                    .queryParam("api_key", apiKey)
                    .queryParam("language", "es-ES")
                    .build()
                    .toString();
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null) {
                Map<String, Object> pelicula = new HashMap<>();
                pelicula.put("id", response.get("id"));
                pelicula.put("titulo", response.get("title"));
                pelicula.put("descripcionCompleta", response.get("overview"));
                pelicula.put("fechaEstreno", response.get("release_date"));
                pelicula.put("puntuacion", response.get("vote_average"));
                pelicula.put("duracion", response.get("runtime"));
                
                // Procesar imagen
                String posterPath = (String) response.get("poster_path");
                if (posterPath != null && !posterPath.isEmpty()) {
                    pelicula.put("imagen", IMAGE_BASE_URL + posterPath);
                } else {
                    pelicula.put("imagen", "/images/no-movie-poster.png");
                }
                
                return pelicula;
            }
            
            return new HashMap<>();
            
        } catch (Exception e) {
            System.err.println("Error al obtener detalles de película: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Verificar si la API key está configurada
     */
    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.equals("tu_api_key_aqui") && !apiKey.trim().isEmpty();
    }
}