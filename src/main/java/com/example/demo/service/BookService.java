package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class BookService {
    
    // API Key de Google Books (opcional, pero recomendada para más requests)
    @Value("${google.books.api.key:#{null}}")
    private String apiKey;
    
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    
    private final RestTemplate restTemplate;
    
    public BookService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Buscar libros por término de búsqueda
     */
    public List<Map<String, Object>> buscarLibros(String query) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL)
                    .queryParam("q", query)
                    .queryParam("maxResults", 20)
                    .queryParam("orderBy", "relevance")
                    .queryParam("printType", "books")
                    .queryParam("langRestrict", "es"); // Preferir libros en español
            
            // Agregar API key si está disponible
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                builder.queryParam("key", apiKey);
            }
            
            String url = builder.build().toString();
            
            System.out.println("Buscando libros con URL: " + url.replace(apiKey != null ? apiKey : "", "***"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("items")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                
                return items.stream()
                        .map(this::procesarLibro)
                        .filter(libro -> libro.get("titulo") != null) // Filtrar libros sin título
                        .toList();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("Error al buscar libros: " + e.getMessage());
            throw new RuntimeException("Error al conectar con la API de libros", e);
        }
    }
    
    /**
     * Obtener detalles de un libro específico
     */
    public Map<String, Object> obtenerDetallesLibro(String bookId) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + "/" + bookId);
            
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                builder.queryParam("key", apiKey);
            }
            
            String url = builder.build().toString();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null) {
                return procesarDetalleLibro(response);
            }
            
            return new HashMap<>();
            
        } catch (Exception e) {
            System.err.println("Error al obtener detalles de libro: " + e.getMessage());
            throw new RuntimeException("Error al obtener detalles del libro", e);
        }
    }
    
    /**
     * Buscar libros por categoría
     */
    public List<Map<String, Object>> buscarLibrosPorCategoria(String categoria) {
        return buscarLibros("subject:" + categoria);
    }
    
    /**
     * Buscar libros por autor
     */
    public List<Map<String, Object>> buscarLibrosPorAutor(String autor) {
        return buscarLibros("inauthor:" + autor);
    }
    
    /**
     * Obtener libros más vendidos (simulación con libros populares)
     */
    public List<Map<String, Object>> obtenerLibrosPopulares() {
        try {
            // Buscar libros bestsellers o populares
            return buscarLibros("bestseller OR popular OR award winning");
        } catch (Exception e) {
            System.err.println("Error al obtener libros populares: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Procesar datos de libro para formato consistente
     */
    private Map<String, Object> procesarLibro(Map<String, Object> item) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            resultado.put("id", item.get("id"));
            
            // Extraer información del volumen
            @SuppressWarnings("unchecked")
            Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
            
            if (volumeInfo != null) {
                resultado.put("titulo", volumeInfo.get("title"));
                resultado.put("subtitulo", volumeInfo.get("subtitle"));
                
                // Procesar autores
                if (volumeInfo.containsKey("authors")) {
                    @SuppressWarnings("unchecked")
                    List<String> authors = (List<String>) volumeInfo.get("authors");
                    resultado.put("autores", String.join(", ", authors));
                    resultado.put("autor", authors.get(0)); // Primer autor
                } else {
                    resultado.put("autores", "Autor desconocido");
                    resultado.put("autor", "Autor desconocido");
                }
                
                resultado.put("descripcion", limitarTexto((String) volumeInfo.get("description"), 300));
                resultado.put("editorial", volumeInfo.get("publisher"));
                resultado.put("fechaPublicacion", volumeInfo.get("publishedDate"));
                resultado.put("paginas", volumeInfo.get("pageCount"));
                resultado.put("idioma", volumeInfo.get("language"));
                
                // Procesar categorías
                if (volumeInfo.containsKey("categories")) {
                    @SuppressWarnings("unchecked")
                    List<String> categories = (List<String>) volumeInfo.get("categories");
                    resultado.put("categorias", String.join(", ", categories));
                    resultado.put("categoria", categories.get(0)); // Primera categoría
                }
                
                // Procesar calificación
                if (volumeInfo.containsKey("averageRating")) {
                    resultado.put("puntuacion", volumeInfo.get("averageRating"));
                }
                if (volumeInfo.containsKey("ratingsCount")) {
                    resultado.put("totalCalificaciones", volumeInfo.get("ratingsCount"));
                }
                
                // Procesar imagen de portada
                if (volumeInfo.containsKey("imageLinks")) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> imageLinks = (Map<String, String>) volumeInfo.get("imageLinks");
                    
                    String imagen = imageLinks.get("thumbnail");
                    if (imagen == null) imagen = imageLinks.get("smallThumbnail");
                    if (imagen == null) imagen = imageLinks.get("medium");
                    if (imagen == null) imagen = imageLinks.get("large");
                    
                    if (imagen != null) {
                        // Mejorar calidad de imagen
                        imagen = imagen.replace("&edge=curl", "")
                                     .replace("zoom=1", "zoom=2")
                                     .replace("http://", "https://");
                        resultado.put("imagen", imagen);
                    } else {
                        resultado.put("imagen", "/images/no-book-cover.png");
                    }
                } else {
                    resultado.put("imagen", "/images/no-book-cover.png");
                }
                
                // ISBN
                if (volumeInfo.containsKey("industryIdentifiers")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> identifiers = (List<Map<String, String>>) volumeInfo.get("industryIdentifiers");
                    for (Map<String, String> identifier : identifiers) {
                        if ("ISBN_13".equals(identifier.get("type"))) {
                            resultado.put("isbn13", identifier.get("identifier"));
                            break;
                        } else if ("ISBN_10".equals(identifier.get("type"))) {
                            resultado.put("isbn10", identifier.get("identifier"));
                        }
                    }
                }
            }
            
            // Información de venta (si está disponible)
            if (item.containsKey("saleInfo")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> saleInfo = (Map<String, Object>) item.get("saleInfo");
                resultado.put("disponible", "FOR_SALE".equals(saleInfo.get("saleability")));
                
                if (saleInfo.containsKey("listPrice")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> listPrice = (Map<String, Object>) saleInfo.get("listPrice");
                    resultado.put("precio", listPrice.get("amount"));
                    resultado.put("moneda", listPrice.get("currencyCode"));
                }
            }
            
            // Tipo para identificación
            resultado.put("tipo", "libro");
            
        } catch (Exception e) {
            System.err.println("Error procesando libro: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Procesar detalles completos de libro
     */
    private Map<String, Object> procesarDetalleLibro(Map<String, Object> book) {
        Map<String, Object> resultado = procesarLibro(book);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> volumeInfo = (Map<String, Object>) book.get("volumeInfo");
            
            if (volumeInfo != null) {
                // Información adicional para detalles
                resultado.put("descripcionCompleta", volumeInfo.get("description"));
                resultado.put("infoLink", volumeInfo.get("infoLink"));
                resultado.put("previewLink", volumeInfo.get("previewLink"));
                resultado.put("canonicalVolumeLink", volumeInfo.get("canonicalVolumeLink"));
                
                // Dimensiones si están disponibles
                if (volumeInfo.containsKey("dimensions")) {
                    resultado.put("dimensiones", volumeInfo.get("dimensions"));
                }
                
                // Tipo de impresión
                resultado.put("tipoPrint", volumeInfo.get("printType"));
                resultado.put("tipoMaturity", volumeInfo.get("maturityRating"));
            }
            
        } catch (Exception e) {
            System.err.println("Error procesando detalles de libro: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Verificar si la API key está configurada
     */
    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
    
    /**
     * Obtener información de configuración de la API
     */
    public Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("apiKeyConfigured", isApiKeyConfigured());
        config.put("baseUrl", BASE_URL);
        
        if (!isApiKeyConfigured()) {
            config.put("mensaje", "API Key de Google Books no configurada (opcional). Agrega 'google.books.api.key=tu_clave' en application.properties para más requests");
        }
        
        return config;
    }
    
    /**
     * Limitar texto a un número específico de caracteres
     */
    private String limitarTexto(String texto, int limite) {
        if (texto == null || texto.length() <= limite) {
            return texto;
        }
        return texto.substring(0, limite) + "...";
    }
}