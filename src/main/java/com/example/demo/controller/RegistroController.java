package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.entity.Rol;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.Reg.Regis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashSet;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Modelo para el formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Regis());
        return "registro";
    }
    
    // Procesamiento del formulario de registro
    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute("usuario") @Valid Regis registroDTO,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        
        // Si hay errores de validación
        if (result.hasErrors()) {
            model.addAttribute("error", "Error en los datos del formulario.");
            return "registro";
        }
        
        // Verificar si el nombre ya existe
        if (usuarioRepository.existsByNombre(registroDTO.getNombre())) {
            model.addAttribute("error", "El nombre de usuario ya está registrado.");
            return "registro";
        }
        
        // Verificar si el email ya existe (si tienes esta validación)
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "registro";
        }
        
        // Verificar si las contraseñas coinciden
        if (!registroDTO.getPasswordHash().equals(registroDTO.getConfirmPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }
        
        try {
            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(registroDTO.getNombre());
            usuario.setEmail(registroDTO.getEmail());
            usuario.setPassword(passwordEncoder.encode(registroDTO.getPasswordHash()));
            
            // Asignar rol de usuario por defecto
            Rol rolUsuario = rolRepository.findByNombre("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            usuario.setRoles(new HashSet<>(Collections.singletonList(rolUsuario)));
            
            // Guardar usuario
            usuarioRepository.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. ¡Ya puedes iniciar sesión!");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hubo un error al registrar el usuario: " + e.getMessage());
            return "registro";
        }
    }
}