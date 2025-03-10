Sistema de Autenticación CinéLibro
Panel admin:
![image](https://github.com/user-attachments/assets/ed834e97-a6e6-4d78-81ab-824ea0df8020)
Login:

![image](https://github.com/user-attachments/assets/6e449411-eb25-4791-a874-bec68ddc2ebb)

Un sistema completo de autenticación con temática de libros y películas implementado con Spring Boot, Spring Security y MySQL.
📋 Características

✅ Autenticación segura con Spring Security
👥 Gestión de roles (usuario/administrador)
🎨 Interfaz moderna con temática de libros y películas
📝 Registro de nuevos usuarios
🔐 Panel de administración
👤 Edición de perfiles de usuario
🐳 Dockerización completa (aplicación y base de datos)

🚀 Tecnologías

Backend: Java 17, Spring Boot 3.4.2
Frontend: Thymeleaf, Bootstrap 5
Seguridad: Spring Security
Base de Datos: MySQL 8.0
Containerización: Docker, Docker Compose
Gestión de Dependencias: Maven

🏗️ Estructura del Proyecto
Copysrc/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/              # Configuración de seguridad
│   │   ├── controller/          # Controladores
│   │   ├── entity/              # Entidades JPA
│   │   ├── repository/          # Repositorios
│   │   ├── service/             # Servicios
│   │   └── DemoApplication.java # Clase principal
│   └── resources/
│       ├── static/              # Recursos estáticos
│       ├── templates/           # Plantillas HTML
│       └── application.properties
📷 Capturas de Pantalla
Pantalla de Login
Show Image
Pantalla de Registro
Show Image
Página Principal
Show Image
Panel de Administración
Show Image
🔧 Instalación
Con Docker (Recomendado)
bashCopy# Clonar el repositorio
git clone https://github.com/davidvadhir01/Proyecto_individual.git
cd Proyecto_individual

# Iniciar con Docker Compose
docker-compose up -d
Accede a la aplicación en: http://localhost:8080
Sin Docker (Desarrollo)
bashCopy# Clonar el repositorio
git clone https://github.com/davidvadhir01/Proyecto_individual.git
cd Proyecto_individual

# Compilar el proyecto
./mvnw clean package

# Ejecutar la aplicación
java -jar target/demo-0.0.1-SNAPSHOT.jar
👤 Credenciales por Defecto
Administrador:

Usuario: admin
Contraseña: admin123

🔒 Endpoints Principales
Endpoints Web

/login - Página de inicio de sesión
/registro - Formulario de registro
/home - Página principal (requiere autenticación)
/admin - Panel de administración (solo administradores)
/admin/usuarios - Gestión de usuarios (solo administradores)

Endpoints REST

POST /api/auth/register - Registro de usuarios
POST /api/auth/login - Autenticación
GET /api/auth/session - Verificación de sesión

🛠️ Configuración
application.properties
propertiesCopy# Conexión a la base de datos
spring.datasource.url=jdbc:mysql://localhost:3307/tarea2
spring.datasource.username=admin
spring.datasource.password=admin

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
Docker Compose
yamlCopyversion: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: tarea2
      MYSQL_USER: admin
      MYSQL_PASSWORD: admin
    ports:
      - "3307:3306"
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
📝 Aspectos Técnicos

Seguridad: Encriptación BCrypt para contraseñas
Sesiones: Manejo seguro con Spring Security
Validación: Implementación de validación de formularios
Roles: Control de acceso basado en roles (ROLE_USER, ROLE_ADMIN)
Persistencia: ORM con Hibernate

🤝 Contribuir
Si deseas contribuir a este proyecto, por favor:

Haz fork del repositorio
Crea una rama para tu característica (git checkout -b feature/nueva-caracteristica)
Haz commit de tus cambios (git commit -m 'Añadir nueva característica')
Push a la rama (git push origin feature/nueva-caracteristica)
Abre un Pull Request

PRUEBAS CON THUNDER CLIENT
CON LA PAGINA REGISTER:
![image](https://github.com/user-attachments/assets/7e427951-7511-4309-b290-75f926ef41c5)


📄 Licencia
Este proyecto está bajo la Licencia MIT - mira el archivo LICENSE para detalles
✉️ Contacto
David Balderas - GitHub - Email
