-- mysql/init/init.sql - VERSIÓN CORREGIDA
CREATE DATABASE IF NOT EXISTS tarea2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tarea2;

-- PRIMERO: Crear tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SEGUNDO: Crear tabla de usuarios con nombres de columnas consistentes
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  -- ⚠️ NOMBRE CORRECTO CONSISTENTE
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Campos para películas y libros favoritos
    pelicula_favorita VARCHAR(500) NULL,
    libro_favorito VARCHAR(500) NULL,
    
    -- Campos para información de imagen
    imagen LONGBLOB NULL,
    imagen_nombre VARCHAR(255) NULL,
    imagen_tipo VARCHAR(100) NULL,
    
    -- Índices
    INDEX idx_email (email),
    INDEX idx_fecha_creacion (fecha_creacion)
);

-- TERCERO: Crear tabla de relación usuarios-roles
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT,
    rol_id BIGINT,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- CUARTO: Insertar roles básicos (usando IGNORE para evitar duplicados)
INSERT IGNORE INTO roles (nombre, descripcion) VALUES 
('ROLE_ADMIN', 'Administrador del sistema'),
('ROLE_USER', 'Usuario normal');

-- QUINTO: Insertar usuario administrador por defecto
-- Contraseña: admin123 (hasheada con BCrypt)
INSERT IGNORE INTO usuarios (nombre, email, password_hash, pelicula_favorita, libro_favorito) 
VALUES ('admin', 'admin@cinelibro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'El Padrino', 'Cien años de soledad');

-- SEXTO: Asignar rol de admin al usuario admin
INSERT IGNORE INTO usuario_roles (usuario_id, rol_id) 
SELECT u.id, r.id 
FROM usuarios u, roles r 
WHERE u.email = 'admin@cinelibro.com' AND r.nombre = 'ROLE_ADMIN';

-- VERIFICACIÓN: Mostrar que todo está configurado correctamente
SELECT 'Configuración completada correctamente' as status;