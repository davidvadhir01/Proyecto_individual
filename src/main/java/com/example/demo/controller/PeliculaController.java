package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PeliculaController {
    
    @GetMapping("/peliculas")
    public String peliculas(Model model) {
        return "peliculas";
    }
}