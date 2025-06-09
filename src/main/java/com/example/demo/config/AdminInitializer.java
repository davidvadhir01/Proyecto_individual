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
        // Verificar si ya existe un administrador
        long adminCount = usuarioRepository.countByRolesNombre("ROLE_ADMIN");
        System.out.println("Número de administradores encontrados: " + adminCount);
        
        if (adminCount == 0) {
            // Crear rol de administrador si no existe
            Rol adminRol = rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Rol newRol = new Rol();
                        newRol.setNombre("ROLE_ADMIN");
                        return rolRepository.save(newRol);
                    });
            
            // Crear usuario administrador
            Usuario admin = new Usuario();
            admin.setNombre("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(new HashSet<>(Collections.singletonList(adminRol)));
            
            usuarioRepository.save(admin);
            
            System.out.println("Usuario administrador creado con éxito");
        }
    }
}