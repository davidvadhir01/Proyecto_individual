-- En la carpeta mysql/init/init.sql
CREATE DATABASE IF NOT EXISTS tarea2 CHARACTER SET utf8 COLLATE utf8_general_ci;
USE tarea2;

-- PRIMERO: Crear la tabla usuarios completa desde cero
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
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

-- Insertar usuario administrador por defecto
INSERT INTO usuarios (nombre, email, password_hash, pelicula_favorita, libro_favorito) 
VALUES ('admin', 'admin@cinelibro.com', '$2a$10$NQV8QcN7Zs8QJJ5QQ5Q5QeQ5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Q5Qe', 'El Padrino', 'Cien años de soledad')
ON DUPLICATE KEY UPDATE 
    nombre = VALUES(nombre),
    pelicula_favorita = VALUES(pelicula_favorita),
    libro_favorito = VALUES(libro_favorito);

-- Insertar algunos usuarios de ejemplo
INSERT INTO usuarios (nombre, email, password_hash, pelicula_favorita, libro_favorito) VALUES 
('Juan Pérez', 'juan@example.com', '$2a$10$example1', 'Inception', 'Don Quijote'),
('María García', 'maria@example.com', '$2a$10$example2', 'Titanic', 'Orgullo y Prejuicio'),
('Carlos López', 'carlos@example.com', '$2a$10$example3', 'Matrix', 'El Señor de los Anillos')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Crear tabla de roles (si es necesaria)
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar roles básicos
INSERT INTO roles (nombre, descripcion) VALUES 
('ADMIN', 'Administrador del sistema'),
('USER', 'Usuario normal')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- Crear tabla de usuarios_roles (si es necesaria)
CREATE TABLE IF NOT EXISTS usuarios_roles (
    usuario_id BIGINT,
    rol_id BIGINT,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Asignar rol de admin al usuario admin
INSERT INTO usuarios_roles (usuario_id, rol_id) 
SELECT u.id, r.id 
FROM usuarios u, roles r 
WHERE u.email = 'admin@cinelibro.com' AND r.nombre = 'ADMIN'
ON DUPLICATE KEY UPDATE fecha_asignacion = CURRENT_TIMESTAMP;

-- Verificar que todo está funcionando correctamente
SELECT 
    COUNT(*) as total_usuarios,
    COUNT(imagen) as usuarios_con_imagen,
    COUNT(pelicula_favorita) as usuarios_con_pelicula,
    COUNT(libro_favorito) as usuarios_con_libro
FROM usuarios;