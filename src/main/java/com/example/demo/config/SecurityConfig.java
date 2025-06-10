package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // ✅ Rutas públicas (sin autenticación)
                    .requestMatchers("/", "/login", "/registro", "/usuarios/registro", 
                                   "/usuarios/registrar", "/css/**", "/js/**", "/images/**", 
                                   "/api/ping", "/favicon.ico").permitAll()
                    
                    // ✅ Rutas solo para administradores
                    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    
                    // ✅ Todas las demás rutas requieren autenticación
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Deshabilitado para APIs
        
        return http.build();
    }
}