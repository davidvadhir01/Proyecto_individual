package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;
import com.example.demo.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Panel principal de administración
     */
    @GetMapping({"", "/"})
    public String adminHome(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Verificar si es admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/home";
        }

        try {
            Usuario usuarioCompleto = usuarioRepository.findByNombre(userDetails.getUsername())
                    .orElse(null);
            
            model.addAttribute("usuario", userDetails.getUsername());
            model.addAttribute("usuarioCompleto", usuarioCompleto);
            model.addAttribute("isAdmin", true);
            
        } catch (Exception e) {
            System.err.println("Error al cargar datos del admin: " + e.getMessage());
            model.addAttribute("usuario", userDetails.getUsername());
            model.addAttribute("isAdmin", true);
        }

        return "admin";
    }
    
    /**
     * Gestión de usuarios
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Verificar si es admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/home";
        }

        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("username", userDetails.getUsername());
        return "usuarios";
    }

    /**
     * Cargar formulario de edición
     */
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, 
                               @AuthenticationPrincipal UserDetails userDetails, 
                               Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Verificar si es admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/home";
        }

        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "editar_usuario";
    }

    /**
     * Guardar cambios en usuario
     */
    @PostMapping("/usuarios/editar/{id}")
    public String actualizarUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute Usuario usuarioForm,
            @RequestParam(required = false) String password) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Verificar si es admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/home";
        }
        
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }
        
        // Actualiza solo los campos necesarios
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setEmail(usuarioForm.getEmail());
        
        if (password != null && !password.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }
        
        usuarioService.guardar(usuario);
        return "redirect:/admin/usuarios";
    }

    /**
     * Eliminar usuario
     */
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Verificar si es admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/home";
        }

        usuarioService.eliminar(id);
        return "redirect:/admin/usuarios";
    }
}