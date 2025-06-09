package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class TestConexionController {

    private final DataSource dataSource;

    public TestConexionController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/ping")
    public String testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return "✅ Conectado a la base de datos: " + connection.getCatalog();
        } catch (SQLException e) {
            return "❌ Error de conexión: " + e.getMessage();
        }
    }
}

