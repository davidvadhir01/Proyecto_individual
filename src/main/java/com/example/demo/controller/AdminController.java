package com.example.demo.controller;

import com.example.demo.entity.Usuario;
import com.example.demo.service.UsuarioService;

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

    private final UsuarioService usuarioService;

    private final PasswordEncoder passwordEncoder;

    public AdminController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";
    }

    // Cargar formulario de edición
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "editar_usuario"; // Cambiar la vista
    }



    // Guardar cambios en usuario
    @PostMapping("/usuarios/editar/{id}")
public String actualizarUsuario(
        @PathVariable Long id,
        @ModelAttribute Usuario usuarioForm,
        @RequestParam(required = false) String password) {
    
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

    // Eliminar usuario
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/admin/usuarios";
    }
    
}