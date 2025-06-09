package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    // ✅ CORRECCIÓN: Mapear correctamente a la columna password_hash
    @Column(name = "password_hash", nullable = false)
    private String password;

    // Campo para almacenar la imagen de perfil como BLOB
    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    private byte[] imagen;

    // Nuevos campos para películas y libros favoritos
    @Column(name = "pelicula_favorita", length = 500)
    private String peliculaFavorita;

    @Column(name = "libro_favorito", length = 500)
    private String libroFavorito;

    // Campo para almacenar el nombre del archivo de imagen
    @Column(name = "imagen_nombre", length = 255)
    private String imagenNombre;

    // Campo para el tipo MIME de la imagen
    @Column(name = "imagen_tipo", length = 100)
    private String imagenTipo;

    // ✅ CORRECCIÓN: Usar el nombre correcto de la tabla de unión
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",  // ⚠️ NOMBRE CORRECTO SIN GUION BAJO
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    // Constructor vacío
    public Usuario() {}

    // Constructor con parámetros básicos
    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    // Getters y setters existentes
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    // Nuevos getters y setters para los campos agregados
    public String getPeliculaFavorita() {
        return peliculaFavorita;
    }

    public void setPeliculaFavorita(String peliculaFavorita) {
        this.peliculaFavorita = peliculaFavorita;
    }

    public String getLibroFavorito() {
        return libroFavorito;
    }

    public void setLibroFavorito(String libroFavorito) {
        this.libroFavorito = libroFavorito;
    }

    public String getImagenNombre() {
        return imagenNombre;
    }

    public void setImagenNombre(String imagenNombre) {
        this.imagenNombre = imagenNombre;
    }

    public String getImagenTipo() {
        return imagenTipo;
    }

    public void setImagenTipo(String imagenTipo) {
        this.imagenTipo = imagenTipo;
    }

    // Método de utilidad para verificar si tiene imagen
    public boolean tieneImagen() {
        return imagen != null && imagen.length > 0;
    }

    // Método para obtener la URL de la imagen
    public String getImagenUrl() {
        if (tieneImagen()) {
            return "/api/usuarios/" + id + "/imagen";
        }
        return "/images/default-avatar.png"; // Imagen por defecto
    }

    // ✅ MÉTODO ÚTIL: toString para debugging
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + (roles != null ? roles.size() : 0) + " roles" +
                '}';
    }
}