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
public class LibroController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/search.json";
    private final String OPEN_LIBRARY_COVERS_URL = "https://covers.openlibrary.org/b";

    // ========== TU MÉTODO EXISTENTE PARA PÁGINA WEB ==========
    
    @GetMapping("/libros")
    public String libros(Model model) {
        return "libros";
    }

    // ========== MÉTODOS API NUEVOS (AGREGADOS) ==========

    /**
     * API: Buscar libros usando Open Library API
     */
    @GetMapping("/libros/api/buscar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarLibrosApi(@RequestParam String query) {
        try {
            String url = OPEN_LIBRARY_BASE_URL + "?q=" + query.replace(" ", "+") + "&limit=20&fields=key,title,author_name,first_publish_year,isbn,publisher,language,subject,cover_i,edition_count";
            
            System.out.println("Buscando libros en Open Library con query: " + query);
            System.out.println("URL: " + url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("docs")) {
                List<Map<String, Object>> docs = (List<Map<String, Object>>) response.get("docs");
                List<Map<String, Object>> librosFormateados = new ArrayList<>();

                for (Map<String, Object> doc : docs) {
                    if (librosFormateados.size() >= 20) break; // Limitar a 20 resultados
                    
                    Map<String, Object> libro = procesarLibroOpenLibrary(doc);
                    if (libro.get("titulo") != null) { // Solo agregar si tiene título
                        librosFormateados.add(libro);
                    }
                }

                Map<String, Object> resultado = new HashMap<>();
                resultado.put("libros", librosFormateados);
                resultado.put("total", response.get("numFound"));
                resultado.put("fuente", "Open Library API");
                resultado.put("query", query);
                
                System.out.println("Encontrados " + librosFormateados.size() + " libros");
                return ResponseEntity.ok(resultado);
            }

            return ResponseEntity.ok(crearDatosMockLibros(query));

        } catch (RestClientException e) {
            System.err.println("Error al buscar libros en Open Library: " + e.getMessage());
            return ResponseEntity.ok(crearDatosMockLibros(query));
        } catch (Exception e) {
            System.err.println("Error general en búsqueda de libros: " + e.getMessage());
            return ResponseEntity.ok(crearDatosMockLibros(query));
        }
    }

    /**
     * API: Obtener libros guardados en la base de datos
     */
    @GetMapping("/libros/api/mis-libros")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerMisLibros() {
        try {
            // Por ahora retornamos una lista vacía ya que no tienes servicio de base de datos
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("libros", new ArrayList<>());
            resultado.put("total", 0);
            resultado.put("mensaje", "Funcionalidad de base de datos no implementada aún");
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            System.err.println("Error al obtener mis libros: " + e.getMessage());
            return ResponseEntity.ok(new HashMap<>());
        }
    }

    /**
     * API: Agregar libro a favoritos/base de datos
     */
    @PostMapping("/libros/api/agregar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarLibro(@RequestBody Map<String, Object> libroData) {
        try {
            // Por ahora solo confirmamos que se recibieron los datos
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Libro agregado a favoritos (funcionalidad completa pendiente)");
            respuesta.put("titulo", libroData.get("titulo"));
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            System.err.println("Error al agregar libro: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al agregar libro: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Procesar libro de Open Library API
     */
    private Map<String, Object> procesarLibroOpenLibrary(Map<String, Object> doc) {
        Map<String, Object> libro = new HashMap<>();
        
        try {
            // ID único de Open Library
            libro.put("id", doc.get("key"));
            
            // Título
            libro.put("titulo", doc.get("title"));
            
            // Autores
            List<String> autores = (List<String>) doc.get("author_name");
            if (autores != null && !autores.isEmpty()) {
                libro.put("autores", String.join(", ", autores));
                libro.put("autor", autores.get(0)); // Primer autor
            } else {
                libro.put("autores", "Autor desconocido");
                libro.put("autor", "Autor desconocido");
            }
            
            // Año de publicación
            Object firstPublishYear = doc.get("first_publish_year");
            if (firstPublishYear != null) {
                libro.put("fechaPublicacion", firstPublishYear.toString());
                libro.put("año", firstPublishYear);
            }
            
            // Editorial
            List<String> publishers = (List<String>) doc.get("publisher");
            if (publishers != null && !publishers.isEmpty()) {
                libro.put("editorial", publishers.get(0));
            }
            
            // Idiomas
            List<String> languages = (List<String>) doc.get("language");
            if (languages != null && !languages.isEmpty()) {
                libro.put("idioma", languages.get(0));
            } else {
                libro.put("idioma", "en"); // Por defecto inglés
            }
            
            // Número de ediciones
            Object editionCount = doc.get("edition_count");
            if (editionCount != null) {
                libro.put("ediciones", editionCount);
            }
            
            // ISBN
            List<String> isbns = (List<String>) doc.get("isbn");
            if (isbns != null && !isbns.isEmpty()) {
                libro.put("isbn", isbns.get(0));
                libro.put("isbn_list", isbns);
            }
            
            // Categorías/Materias
            List<String> subjects = (List<String>) doc.get("subject");
            if (subjects != null && !subjects.isEmpty()) {
                // Tomar solo las primeras 3 categorías para no saturar
                List<String> topSubjects = subjects.subList(0, Math.min(3, subjects.size()));
                libro.put("categorias", String.join(", ", topSubjects));
                libro.put("categoria", topSubjects.get(0));
            }
            
            // Imagen de portada
            Object coverId = doc.get("cover_i");
            if (coverId != null) {
                // Open Library covers: https://covers.openlibrary.org/b/id/{cover_id}-{size}.jpg
                String coverUrl = OPEN_LIBRARY_COVERS_URL + "/id/" + coverId + "-M.jpg";
                libro.put("imagen", coverUrl);
            } else {
                libro.put("imagen", "https://via.placeholder.com/180x270?text=Sin+Portada");
            }
            
            // Descripción (Open Library no proporciona descripción en búsqueda, se podría obtener después)
            libro.put("descripcion", "Descripción disponible en la página de detalles");
            
            // Tipo para identificación
            libro.put("tipo", "libro");
            libro.put("fuente", "Open Library");
            
        } catch (Exception e) {
            System.err.println("Error procesando libro de Open Library: " + e.getMessage());
        }
        
        return libro;
    }

    /**
     * Datos de ejemplo cuando no funciona la API
     */
    private Map<String, Object> crearDatosMockLibros(String query) {
        List<Map<String, Object>> librosMock = Arrays.asList(
            crearLibroMock("OL123456W", "Cien años de soledad", "Gabriel García Márquez", 
                "Una novela emblemática del realismo mágico que narra la historia de la familia Buendía en el pueblo ficticio de Macondo...", 
                "https://covers.openlibrary.org/b/id/8236674-M.jpg"),
            crearLibroMock("OL234567W", "Don Quijote de la Mancha", "Miguel de Cervantes",
                "La historia del ingenioso hidalgo Don Quijote de la Mancha y su fiel escudero Sancho Panza en sus aventuras por España...",
                "https://covers.openlibrary.org/b/id/8233672-M.jpg"),
            crearLibroMock("OL345678W", "1984", "George Orwell",
                "Una distopía sobre el totalitarismo donde Big Brother vigila cada movimiento de los ciudadanos en un estado autoritario...",
                "https://covers.openlibrary.org/b/id/7222246-M.jpg"),
            crearLibroMock("OL456789W", "El Principito", "Antoine de Saint-Exupéry",
                "Un cuento poético sobre la amistad, el amor y la pérdida de la inocencia, narrado por un piloto varado en el desierto...",
                "https://covers.openlibrary.org/b/id/8413495-M.jpg"),
            crearLibroMock("OL567890W", "Harry Potter y la Piedra Filosofal", "J.K. Rowling",
                "El inicio de las aventuras del joven mago Harry Potter cuando descubre que es famoso en el mundo mágico...",
                "https://covers.openlibrary.org/b/id/10521270-M.jpg")
        );

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("libros", librosMock);
        resultado.put("total", librosMock.size());
        resultado.put("fuente", "Datos de ejemplo - Open Library no disponible");
        resultado.put("mensaje", "Usando datos de ejemplo. Open Library API temporalmente no disponible.");
        resultado.put("query", query);
        
        return resultado;
    }

    private Map<String, Object> crearLibroMock(String id, String titulo, String autor, String descripcion, String imagen) {
        Map<String, Object> libro = new HashMap<>();
        libro.put("id", id);
        libro.put("titulo", titulo);
        libro.put("autores", autor);
        libro.put("autor", autor);
        libro.put("descripcion", descripcion);
        libro.put("fechaPublicacion", "2023");
        libro.put("editorial", "Editorial Ejemplo");
        libro.put("paginas", 300);
        libro.put("categorias", "Ficción, Literatura");
        libro.put("categoria", "Ficción");
        libro.put("idioma", "es");
        libro.put("imagen", imagen);
        libro.put("tipo", "libro");
        libro.put("fuente", "Open Library");
        return libro;
    }

    private String convertirAutores(Object autores) {
        if (autores instanceof List) {
            List<String> listaAutores = (List<String>) autores;
            return String.join(", ", listaAutores);
        }
        return autores != null ? autores.toString() : "Autor desconocido";
    }
}