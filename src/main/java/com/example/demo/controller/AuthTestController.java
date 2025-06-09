package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthTestController {
    
    @GetMapping("/who-am-i")
    public String whoAmI(Authentication authentication) {
        if (authentication == null) {
            return "No estás autenticado";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>Información de autenticación</h3>");
        sb.append("<p>Usuario: ").append(authentication.getName()).append("</p>");
        sb.append("<p>Tipo de autenticación: ").append(authentication.getClass().getName()).append("</p>");
        sb.append("<p>Detalles: ").append(authentication.getDetails()).append("</p>");
        
        sb.append("<h4>Autoridades/Roles:</h4>");
        sb.append("<ul>");
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            sb.append("<li>").append(auth.getAuthority()).append("</li>");
        }
        sb.append("</ul>");
        
        return sb.toString();
    }
    
    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminOnly() {
        return "¡Si puedes ver esto, tienes el rol de ADMIN!";
    }
}