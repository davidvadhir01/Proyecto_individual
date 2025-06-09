package com.example.demo.config;

import com.example.demo.entity.Usuario;
import com.example.demo.entity.Rol;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.RolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    // Creamos el passwordEncoder directamente en lugar de inyectarlo
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Iniciando AdminInitializer...");
        
        try {
            // ✅ PASO 1: Verificar y crear roles si no existen
            Rol adminRol = crearRolSiNoExiste("ROLE_ADMIN", "Administrador del sistema");
            Rol userRol = crearRolSiNoExiste("ROLE_USER", "Usuario normal");
            
            // ✅ PASO 2: Verificar si ya existe un administrador
            long adminCount = usuarioRepository.countByRolesNombre("ROLE_ADMIN");
            System.out.println("📊 Número de administradores encontrados: " + adminCount);
            
            if (adminCount == 0) {
                System.out.println("👑 Creando usuario administrador por defecto...");
                
                // ✅ PASO 3: Crear usuario administrador
                Usuario admin = new Usuario();
                admin.setNombre("admin");
                admin.setEmail("admin@cinelibro.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setPeliculaFavorita("El Padrino");
                admin.setLibroFavorito("Cien años de soledad");
                admin.setRoles(new HashSet<>(Collections.singletonList(adminRol)));
                
                Usuario savedAdmin = usuarioRepository.save(admin);
                System.out.println("✅ Usuario administrador creado exitosamente con ID: " + savedAdmin.getId());
                System.out.println("📧 Email: " + savedAdmin.getEmail());
                System.out.println("🔑 Contraseña: admin123");
                
            } else {
                System.out.println("✅ Usuario administrador ya existe, saltando creación");
            }
            
            // ✅ PASO 4: Verificar configuración final
            verificarConfiguracion();
            
        } catch (Exception e) {
            System.err.println("❌ Error en AdminInitializer: " + e.getMessage());
            e.printStackTrace();
            // No lanzamos la excepción para que no impida el inicio de la aplicación
        }
        
        System.out.println("🏁 AdminInitializer completado");
    }
    
    /**
     * Crea un rol si no existe
     */
    private Rol crearRolSiNoExiste(String nombreRol, String descripcion) {
        Optional<Rol> rolExistente = rolRepository.findByNombre(nombreRol);
        
        if (rolExistente.isPresent()) {
            System.out.println("✅ Rol '" + nombreRol + "' ya existe");
            return rolExistente.get();
        } else {
            System.out.println("🆕 Creando rol: " + nombreRol);
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(nombreRol);
            // Solo establecer descripción si el setter existe
            try {
                // Verificamos si el método setDescripcion existe
                nuevoRol.getClass().getMethod("setDescripcion", String.class);
                // Si existe, lo llamamos usando reflexión para evitar errores de compilación
                nuevoRol.getClass().getMethod("setDescripcion", String.class).invoke(nuevoRol, descripcion);
            } catch (Exception e) {
                // Si no existe el método, simplemente continuamos sin descripción
                System.out.println("ℹ️ Rol sin campo descripción");
            }
            
            Rol savedRol = rolRepository.save(nuevoRol);
            System.out.println("✅ Rol '" + nombreRol + "' creado con ID: " + savedRol.getId());
            return savedRol;
        }
    }
    
    /**
     * Verifica que la configuración esté correcta
     */
    private void verificarConfiguracion() {
        try {
            long totalUsuarios = usuarioRepository.count();
            long totalRoles = rolRepository.count();
            long totalAdmins = usuarioRepository.countByRolesNombre("ROLE_ADMIN");
            
            System.out.println("📈 RESUMEN DE CONFIGURACIÓN:");
            System.out.println("   👥 Total usuarios: " + totalUsuarios);
            System.out.println("   🏷️ Total roles: " + totalRoles);
            System.out.println("   👑 Total administradores: " + totalAdmins);
            
            if (totalAdmins > 0) {
                System.out.println("✅ Sistema configurado correctamente");
            } else {
                System.out.println("⚠️ Advertencia: No hay administradores en el sistema");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error verificando configuración: " + e.getMessage());
        }
    }
}