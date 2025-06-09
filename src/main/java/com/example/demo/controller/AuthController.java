package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Reg.Regis;

@Controller
public class AuthController {

    // Redirige la página principal al login
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
    
    // Muestra la página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    // En tu AuthController.java, agrega:
@GetMapping("/forgot-password")
public String mostrarFormularioRecuperacion() {
    return "forgot_password"; // Crear esta plantilla
}

// Método para procesar solicitudes de recuperación de contraseña
@PostMapping("/forgot-password")
public String procesarRecuperacionContrasena(@RequestParam String email, RedirectAttributes redirectAttributes) {
    // Lógica para enviar correo de recuperación
    redirectAttributes.addFlashAttribute("success", "Te hemos enviado un correo con instrucciones para restablecer tu contraseña.");
    return "redirect:/login";
}

    // Muestra la página de registro y añade el modelo necesario
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Regis());
        return "registro";
    }
    
    
    // Muestra la página principal después del login
    @GetMapping("/dashboard")
    public String dashboard() {
        return "home";
    }
}