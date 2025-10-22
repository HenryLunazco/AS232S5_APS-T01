# HinoPE - Backend API

Backend API REST para el sistema de gestión de Hino Perú. Desarrollado con Spring Boot WebFlux y arquitectura reactiva.

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring WebFlux** (Programación reactiva)
- **R2DBC PostgreSQL** (Acceso reactivo a base de datos)
- **Spring Security** (Autenticación JWT)
- **Lombok** (Reducción de boilerplate)
- **JWT (jjwt)** (Tokens de autenticación)
- **BCrypt** (Hash de contraseñas)

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- PostgreSQL 12+ (o cuenta en Neon)
- Variables de entorno configuradas

## ⚙️ Configuración

### 1. Variables de Entorno

Copia el archivo `.env.example` a `.env` y configura las variables:

```bash
cp .env.example .env
```

Edita el archivo `.env` con tus credenciales:

```env
# Database Configuration (Neon PostgreSQL)
DB_URL=r2dbc:postgresql://your-host:5432/your-database?sslmode=require
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Server Configuration
SERVER_PORT=8080

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here_minimum_256_bits
JWT_EXPIRATION=86400000

# File Upload Configuration
FILE_UPLOAD_DIR=public/uploads
FILE_MAX_SIZE_IMAGES=10485760
FILE_MAX_SIZE_DOCUMENTS=20971520

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Logging Configuration
LOG_LEVEL=DEBUG
LOG_LEVEL_R2DBC=DEBUG
LOG_LEVEL_QUERY=DEBUG
LOG_LEVEL_PARAM=DEBUG
```

### 2. Base de Datos

El proyecto está configurado para usar la base de datos existente de Neon PostgreSQL. Asegúrate de que las tablas estén creadas según el schema en `AS232S5_APS_T01-fe/database/schema.sql`.

## 🏃 Ejecución

### Desarrollo

```bash
mvn spring-boot:run
```

### Producción

```bash
mvn clean package
java -jar target/HinoPE-0.0.1-SNAPSHOT.jar
```

El servidor estará disponible en `http://localhost:8080`

## 📚 Documentación de API

### Swagger UI

La documentación interactiva de la API está disponible en:

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

Desde Swagger UI puedes:
- Ver todos los endpoints disponibles
- Probar los endpoints directamente desde el navegador
- Ver los modelos de datos (DTOs)
- Autenticarte con JWT para probar endpoints protegidos

#### Cómo usar Swagger con autenticación:

1. Primero, usa el endpoint `POST /api/auth/login` para obtener un token JWT
2. Copia el token de la respuesta
3. Haz clic en el botón "Authorize" (🔒) en la parte superior derecha
4. Pega el token en el campo "Value" (sin el prefijo "Bearer")
5. Haz clic en "Authorize" y luego "Close"
6. Ahora puedes probar todos los endpoints protegidos

### Autenticación

Todos los endpoints (excepto `/api/auth/login` y `/api/public/**`) requieren autenticación JWT.

**Header requerido:**
```
Authorization: Bearer <token>
```

#### POST /api/auth/login

Autenticar usuario y obtener token JWT.

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "contraseña"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "nombre": "Usuario",
      "email": "usuario@example.com",
      "rol": "admin"
    }
  },
  "timestamp": "2025-10-15T10:30:00"
}
```

### Vehículos

#### GET /api/vehicles
Obtener todos los vehículos (con filtros opcionales).

**Query Parameters:**
- `type` (opcional): Filtrar por tipo (camion, bus)
- `status` (opcional): Filtrar por estado (disponible, reservado, vendido)

#### GET /api/vehicles/{id}
Obtener vehículo por ID.

#### POST /api/vehicles
Crear nuevo vehículo.

#### PUT /api/vehicles/{id}
Actualizar vehículo.

#### DELETE /api/vehicles/{id}
Eliminar vehículo.

#### GET /api/vehicles/stats
Obtener estadísticas de vehículos.

### Usuarios

#### GET /api/users
Obtener todos los usuarios.

#### GET /api/users/{id}
Obtener usuario por ID.

#### POST /api/users
Crear nuevo usuario.

#### PUT /api/users/{id}
Actualizar usuario.

#### DELETE /api/users/{id}
Eliminar usuario.

#### GET /api/users/stats
Obtener estadísticas de usuarios.

### Cotizaciones

#### GET /api/quotes
Obtener todas las cotizaciones (con filtros opcionales).

**Query Parameters:**
- `status` (opcional): Filtrar por estado
- `priority` (opcional): Filtrar por prioridad

#### GET /api/quotes/{id}
Obtener cotización por ID.

#### POST /api/quotes
Crear nueva cotización.

#### PUT /api/quotes/{id}
Actualizar cotización.

#### PUT /api/quotes/{id}/assign
Asignar asesor a cotización.

**Query Parameters:**
- `advisorId`: ID del asesor a asignar

#### DELETE /api/quotes/{id}
Eliminar cotización.

#### GET /api/quotes/stats
Obtener estadísticas de cotizaciones.

### Notificaciones

#### GET /api/notifications
Obtener todas las notificaciones (con filtros opcionales).

**Query Parameters:**
- `read` (opcional): Filtrar por estado de lectura (true/false)
- `type` (opcional): Filtrar por tipo

#### GET /api/notifications/{id}
Obtener notificación por ID.

#### POST /api/notifications
Crear nueva notificación.

#### PUT /api/notifications/{id}/read
Marcar notificación como leída.

#### PUT /api/notifications/read-all
Marcar todas las notificaciones como leídas.

#### DELETE /api/notifications/{id}
Eliminar notificación.

#### GET /api/notifications/stats
Obtener estadísticas de notificaciones.

### Carga de Archivos

#### POST /api/upload
Subir archivo (imagen o documento).

**Form Data:**
- `file`: Archivo a subir
- `type` (opcional): Tipo de archivo (image, document) - default: image

**Response:**
```json
{
  "success": true,
  "message": "Archivo subido exitosamente",
  "data": {
    "url": "/uploads/images/uuid.jpg",
    "filename": "original-filename.jpg"
  }
}
```

### Endpoints Públicos (Sin Autenticación)

#### GET /api/public/vehicles
Obtener vehículos disponibles.

**Query Parameters:**
- `type` (opcional): Filtrar por tipo

#### GET /api/public/vehicles/{id}
Obtener vehículo por ID.

#### GET /api/public/advisors
Obtener lista de asesores activos.

## 🔒 Seguridad

- **JWT**: Tokens con expiración de 24 horas
- **BCrypt**: Hash de contraseñas con strength 10
- **CORS**: Configurado para permitir requests desde el frontend
- **Validación**: Validación de datos en todos los endpoints

## 📁 Estructura del Proyecto

```
HinoPE/
├── src/main/java/vg/edu/pe/HinoPE/
│   ├── config/              # Configuraciones (CORS, Security, R2DBC)
│   ├── controller/          # Controladores REST
│   ├── service/             # Lógica de negocio
│   ├── repository/          # Repositorios R2DBC
│   ├── model/
│   │   ├── entity/          # Entidades de base de datos
│   │   ├── dto/             # DTOs para transferencia de datos
│   │   └── enums/           # Enumeraciones
│   ├── security/            # JWT y filtros de seguridad
│   ├── exception/           # Excepciones personalizadas
│   └── util/                # Utilidades (Password, JWT)
├── src/main/resources/
│   └── application.yml      # Configuración de la aplicación
├── .env                     # Variables de entorno (no subir a git)
├── .env.example             # Ejemplo de variables de entorno
└── pom.xml                  # Dependencias Maven
```

## 🐛 Manejo de Errores

Todas las respuestas de error siguen el formato:

```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null,
  "timestamp": "2025-10-15T10:30:00"
}
```

**Códigos de estado HTTP:**
- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Recurso eliminado exitosamente
- `400 Bad Request`: Error de validación
- `401 Unauthorized`: No autenticado o token inválido
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error interno del servidor

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

## 📝 Notas

- El proyecto usa programación reactiva con Mono/Flux
- Todas las operaciones de base de datos son no bloqueantes
- Los passwords nunca se retornan en las respuestas de la API
- Los archivos subidos se almacenan en `public/uploads/`

## 👥 Autores

Proyecto desarrollado para Hino Perú

## 📄 Licencia

Este proyecto es privado y confidencial.
