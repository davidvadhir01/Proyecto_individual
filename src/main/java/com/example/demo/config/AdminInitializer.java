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
    
    // Creamos el passwordEncoder directamente
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Iniciando AdminInitializer...");
        
        try {
            // ✅ PASO 1: Crear roles si no existen
            Rol adminRol = crearRolSiNoExiste("ROLE_ADMIN");
            Rol userRol = crearRolSiNoExiste("ROLE_USER");
            
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
                System.out.println("✅ Usuario administrador creado exitosamente!");
                System.out.println("📧 Email: " + savedAdmin.getEmail());
                System.out.println("🔑 Usuario: admin");
                System.out.println("🔑 Contraseña: admin123");
                
            } else {
                System.out.println("✅ Usuario administrador ya existe");
            }
            
            // ✅ PASO 4: Verificar configuración
            verificarConfiguracion();
            
        } catch (Exception e) {
            System.err.println("❌ Error en AdminInitializer: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("🏁 AdminInitializer completado");
    }
    
    private Rol crearRolSiNoExiste(String nombreRol) {
        Optional<Rol> rolExistente = rolRepository.findByNombre(nombreRol);
        
        if (rolExistente.isPresent()) {
            System.out.println("✅ Rol '" + nombreRol + "' ya existe");
            return rolExistente.get();
        } else {
            System.out.println("🆕 Creando rol: " + nombreRol);
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(nombreRol);
            
            Rol savedRol = rolRepository.save(nuevoRol);
            System.out.println("✅ Rol '" + nombreRol + "' creado con ID: " + savedRol.getId());
            return savedRol;
        }
    }
    
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
                System.out.println("🌐 Accede a: http://localhost:8080");
                System.out.println("👤 Usuario: admin | 🔑 Contraseña: admin123");
            } else {
                System.out.println("⚠️ Advertencia: No hay administradores en el sistema");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error verificando configuración: " + e.getMessage());
        }
    }
}