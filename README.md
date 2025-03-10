Sistema de Autenticación con Spring Boot y Docker
Este proyecto implementa un sistema completo de autenticación de usuarios utilizando Spring Boot, Spring Security y MySQL. El sistema permite registro de usuarios, inicio de sesión, gestión de roles (usuario/administrador) y un panel de administración para gestionar usuarios.
Características

Sistema de autenticación seguro: Implementación completa con Spring Security
Gestión de roles: Manejo de usuarios con diferentes niveles de acceso (usuario/administrador)
Interfaz estilizada: Temática de libros y películas con animaciones y elementos decorativos
Registro de nuevos usuarios: Formulario completo con validaciones
Panel de administración: Gestión de usuarios (listar, editar, eliminar)
Edición de perfiles: Usuarios pueden editar su información personal
Dockerización completa: Aplicación y base de datos empaquetadas para fácil despliegue
Endpoints REST: API para autenticación y gestión de usuarios

Tecnologías Utilizadas

Backend: Java 17, Spring Boot 3.4.2
Seguridad: Spring Security
Persistencia: Spring Data JPA, Hibernate
Base de Datos: MySQL 8.0
Frontend: Thymeleaf, Bootstrap 5, CSS personalizado
Containerización: Docker, Docker Compose
Gestión de Dependencias: Maven

Estructura del Proyecto
CopyProyectoAutenticacion/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/              # Configuración de seguridad y aplicación
│   │   │   ├── controller/          # Controladores REST y MVC
│   │   │   ├── entity/              # Entidades JPA (Usuario, Rol)
│   │   │   ├── repository/          # Repositorios JPA
│   │   │   ├── service/             # Lógica de negocio
│   │   │   ├── SistemaAutenticacion/ # Componentes específicos de autenticación
│   │   │   └── DemoApplication.java # Clase principal
│   │   └── resources/
│   │       ├── static/              # Recursos estáticos (CSS, JS)
│   │       ├── templates/           # Plantillas Thymeleaf (login, admin, etc.)
│   │       └── application.properties # Configuración de la aplicación
├── Dockerfile                        # Configuración de imagen Docker
├── docker-compose.yml                # Configuración de Docker Compose
└── pom.xml                           # Dependencias de Maven
Detalles Técnicos
Endpoints Implementados
Endpoints MVC (Basados en vistas)

GET / - Redirección a la página de login
GET /login - Página de inicio de sesión
GET /registro - Formulario de registro
GET /home - Página principal de usuario autenticado
GET /admin - Panel de administración (solo accesible para administradores)
GET /admin/usuarios - Listado de usuarios (solo administradores)
GET /admin/usuarios/editar/{id} - Editar usuario (solo administradores)
GET /perfil/editar - Edición del perfil del usuario actual

Endpoints REST

POST /api/auth/register - Registro de usuarios vía API
POST /api/auth/login - Autenticación vía API
GET /api/auth/session - Verificación del estado de sesión
GET /api/ping - Prueba de conexión a la base de datos

Seguridad

Encriptación de contraseñas con BCrypt
Manejo de sesiones HTTP seguras
Control de acceso basado en roles
Protección CSRF configurada
Validación de formularios tanto en cliente como en servidor

Modelo de Datos
Usuario
CampoTipoDescripciónidLongIdentificador úniconombreStringNombre de usuarioemailStringCorreo electrónico (único)passwordStringContraseña encriptada con BCryptimagenbyte[]Imagen de perfil (opcional)rolesSet<Rol>Roles asignados al usuario
Rol
CampoTipoDescripciónidLongIdentificador úniconombreStringNombre del rol (ej: ROLE_ADMIN)usuariosSet<Usuario>Usuarios con este rol
Capturas de Pantalla
Pantalla de Login
Show Image
Pantalla de Registro
Show Image
Página Principal (Home)
Show Image
Panel de Administración
Show Image
Gestión de Usuarios
Show Image
Edición de Usuario
Show Image
Requisitos Previos

Docker y Docker Compose
JDK 17 (solo para desarrollo local)
Maven (solo para desarrollo local)

Instalación y Ejecución
Usando Docker (Recomendado)

Clona este repositorio:

bashCopygit clone https://github.com/tu-usuario/proyecto-autenticacion.git
cd proyecto-autenticacion

Ejecuta la aplicación con Docker Compose:

bashCopydocker-compose up -d

Accede a la aplicación en tu navegador:

Copyhttp://localhost:8080
Desarrollo Local (Sin Docker)

Clona el repositorio:

bashCopygit clone https://github.com/tu-usuario/proyecto-autenticacion.git
cd proyecto-autenticacion

Configura una base de datos MySQL:

Crea una base de datos llamada tarea2
Actualiza las credenciales en src/main/resources/application.properties


Compila y ejecuta la aplicación:

bashCopy./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar

Accede a la aplicación en tu navegador:

Copyhttp://localhost:8080
Configuración de Docker
Dockerfile
dockerfileCopyFROM eclipse-temurin:17-jdk-focal as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-focal
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
docker-compose.yml
yamlCopyversion: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mysql
    restart: always
    command: --default-authentication-plugin=mysql_native_password
    environment:
      MYSQL_DATABASE: tarea2
      MYSQL_USER: admin
      MYSQL_PASSWORD: admin
      MYSQL_ROOT_PASSWORD: admin
    ports:
      - "3307:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./mysql/init:/docker-entrypoint-initdb.d
    networks:
      - app-network

  app:
    build: .
    container_name: springboot-app
    restart: on-failure
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/tarea2?allowPublicKeyRetrieval=true&useSSL=false
      - SPRING_DATASOURCE_USERNAME=admin
      - SPRING_DATASOURCE_PASSWORD=admin
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
      - SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  mysql-data:
Credenciales por Defecto
Una vez que la aplicación esté en funcionamiento, puedes acceder con las siguientes credenciales:
Usuario Administrador:

Usuario: admin
Contraseña: admin123

Usuario Regular:

Puedes crear uno desde la página de registro

Solución de Problemas
Puerto en uso
Si encuentras un error como "Ports are not available" al ejecutar Docker Compose:

Verifica qué proceso está usando el puerto (por ejemplo, 3307 para MySQL):

bashCopynetstat -ano | findstr :3307   # Windows
sudo lsof -i :3307             # Linux/Mac

Detén ese proceso o cambia el puerto en el archivo docker-compose.yml.

Errores de conexión a la base de datos
Si la aplicación no puede conectarse a la base de datos:

Verifica que el contenedor de MySQL esté en ejecución:

bashCopydocker ps

Comprueba los logs del contenedor de MySQL:

bashCopydocker logs mysql
Aspectos de Seguridad Implementados

Encriptación de contraseñas: Las contraseñas no se almacenan en texto plano, sino que se encriptan usando BCrypt.
Control de acceso basado en roles: Diferentes niveles de acceso para usuarios y administradores.
Protección contra CSRF: Implementado para prevenir ataques de falsificación de solicitudes.
Sesiones seguras: Configuración para mantener la seguridad de las sesiones de usuario.
Validación de datos: Validación tanto en cliente como en servidor para garantizar la integridad de los datos.

Decisiones de Diseño
Interfaz de Usuario

Se implementó una interfaz temática de libros y películas ("CinéLibro") con animaciones y elementos decorativos.
Diseño responsivo utilizando Bootstrap 5 para compatibilidad con dispositivos móviles.
Uso de colores, fuentes y elementos que evocan el mundo de la literatura y el cine.

Arquitectura

Patrón MVC (Modelo-Vista-Controlador) con Spring MVC.
Servicios RESTful para operaciones de autenticación.
Separación clara de responsabilidades entre capas (entidades, repositorios, servicios, controladores).

Persistencia

Base de datos relacional MySQL para almacenamiento de datos.
Spring Data JPA para acceso a datos.
Hibernate como ORM (Object-Relational Mapping).

Autor
David Balderas