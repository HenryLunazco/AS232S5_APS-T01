# HinoPE - Backend API

Backend API REST para el sistema de gestión de Hino Perú. Desarrollado con Spring Boot WebFlux y arquitectura reactiva.

## 📑 Índice

- [Tecnologías](#-tecnologías)
- [Características](#-características)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración](#️-configuración)
- [Ejecución](#-ejecución)
- [Documentación de API](#-documentación-de-api)
- [Módulos](#-módulos)
- [Seguridad](#-seguridad)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Testing](#-testing)
- [Documentación Adicional](#-documentación-adicional)
- [Contribuir](#-contribuir)

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring WebFlux** (Programación reactiva)
- **R2DBC PostgreSQL** (Acceso reactivo a base de datos)
- **Spring Security** (Autenticación JWT)
- **Lombok** (Reducción de boilerplate)
- **JWT (jjwt 0.11.5)** (Tokens de autenticación)
- **SpringDoc OpenAPI** (Documentación Swagger)
- **BCrypt** (Hash de contraseñas - preparado)

## ✨ Características

- ✅ **Arquitectura Reactiva**: Programación no bloqueante con WebFlux
- ✅ **Autenticación JWT**: Sistema de tokens stateless
- ✅ **API RESTful**: Endpoints bien estructurados y documentados
- ✅ **Documentación Swagger**: Interfaz interactiva para probar la API
- ✅ **Validación de Datos**: Validación automática con Bean Validation
- ✅ **Manejo de Errores**: Respuestas estandarizadas y logging
- ✅ **CORS Configurado**: Listo para integración con frontend
- ✅ **Carga de Archivos**: Soporte para imágenes y documentos
- ✅ **Base de Datos Reactiva**: R2DBC para acceso no bloqueante a PostgreSQL

## 📋 Requisitos Previos

- **Java 17** o superior
- **Maven 3.6+**
- **PostgreSQL 12+** (o cuenta en Neon)
- **Git** (para clonar el repositorio)

### Herramientas Recomendadas

- **IDE**: IntelliJ IDEA, Eclipse, o VS Code con extensiones Java
- **Cliente API**: Postman, Insomnia, o usar Swagger UI
- **Cliente DB**: DBeaver, pgAdmin, o TablePlus

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

El proyecto está configurado para usar PostgreSQL (compatible con Neon). 

**Tablas requeridas**:
- `users` - Usuarios del sistema
- `vehicles` - Vehículos disponibles
- `quotes` - Cotizaciones
- `notifications` - Notificaciones del sistema

> **Nota**: Asegúrate de que las tablas estén creadas antes de ejecutar la aplicación.

## 🏃 Ejecución

### Modo Desarrollo

```bash
# Ejecutar con Maven
mvn spring-boot:run

# O con Maven Wrapper (Windows)
mvnw.cmd spring-boot:run

# O con Maven Wrapper (Linux/Mac)
./mvnw spring-boot:run
```

### Modo Producción

```bash
# 1. Compilar el proyecto
mvn clean package

# 2. Ejecutar el JAR
java -jar target/HinoPE-0.0.1-SNAPSHOT.jar
```

### Verificar que está funcionando

El servidor estará disponible en `http://localhost:8080`

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 📚 Documentación de API

### Swagger UI (Recomendado)

La documentación interactiva de la API está disponible en:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

#### Características de Swagger UI

- 📖 Ver todos los endpoints disponibles
- 🧪 Probar endpoints directamente desde el navegador
- 📦 Ver modelos de datos (DTOs)
- 🔐 Autenticación JWT integrada
- 📝 Ejemplos de request/response

#### Cómo usar Swagger con autenticación

1. **Login**: Usa `POST /api/auth/login` para obtener un token JWT
   ```json
   {
     "email": "usuario@ejemplo.com",
     "password": "contraseña"
   }
   ```

2. **Copiar Token**: Copia el token de la respuesta

3. **Autorizar**: 
   - Click en el botón "Authorize" 🔒 (esquina superior derecha)
   - Pega el token (sin el prefijo "Bearer")
   - Click en "Authorize" y luego "Close"

4. **Probar**: Ahora puedes probar todos los endpoints protegidos

## 🔐 Autenticación

### Endpoints Públicos (Sin autenticación)

```
POST   /api/auth/login          # Login de usuarios
GET    /api/public/**           # Recursos públicos
GET    /swagger-ui/**           # Documentación Swagger
```

### Endpoints Protegidos (Requieren JWT)

Todos los demás endpoints bajo `/api/**` requieren autenticación JWT.

**Header requerido:**
```http
Authorization: Bearer <tu_token_jwt>
```

### Obtener Token JWT

**Endpoint**: `POST /api/auth/login`

**Request**:
```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña"
}
```

**Response Exitosa** (200):
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "nombre": "Usuario Ejemplo",
      "email": "usuario@ejemplo.com",
      "rol": "admin",
      "telefono": "+51 999 999 999",
      "especialidad": "Ventas",
      "estado": "activo"
    }
  },
  "timestamp": "2025-10-21T10:30:00"
}
```

**Response Error** (401):
```json
{
  "success": false,
  "message": "Credenciales incorrectas",
  "data": null,
  "timestamp": "2025-10-21T10:30:00"
}
```

### Roles de Usuario

| Rol | Descripción |
|-----|-------------|
| `admin` | Administrador con acceso completo |
| `asesor` | Asesor de ventas |
| `driver` | Conductor |

## 📦 Módulos

### 👤 Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| POST | `/api/users` | Crear nuevo usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |
| GET | `/api/users/stats` | Estadísticas de usuarios |

### 🚛 Vehículos

| Método | Endpoint | Descripción | Query Params |
|--------|----------|-------------|--------------|
| GET | `/api/vehicles` | Listar vehículos | `type`, `status` |
| GET | `/api/vehicles/{id}` | Obtener vehículo | - |
| POST | `/api/vehicles` | Crear vehículo | - |
| PUT | `/api/vehicles/{id}` | Actualizar vehículo | - |
| DELETE | `/api/vehicles/{id}` | Eliminar vehículo | - |
| GET | `/api/vehicles/stats` | Estadísticas | - |

**Filtros disponibles**:
- `type`: `camion`, `bus`
- `status`: `disponible`, `reservado`, `vendido`

### 📋 Cotizaciones

| Método | Endpoint | Descripción | Query Params |
|--------|----------|-------------|--------------|
| GET | `/api/quotes` | Listar cotizaciones | `status`, `priority` |
| GET | `/api/quotes/{id}` | Obtener cotización | - |
| POST | `/api/quotes` | Crear cotización | - |
| PUT | `/api/quotes/{id}` | Actualizar cotización | - |
| PUT | `/api/quotes/{id}/assign` | Asignar asesor | `advisorId` |
| DELETE | `/api/quotes/{id}` | Eliminar cotización | - |
| GET | `/api/quotes/stats` | Estadísticas | - |

**Estados**: `pendiente`, `en_proceso`, `aprobada`, `rechazada`  
**Prioridades**: `baja`, `media`, `alta`, `urgente`

### 🔔 Notificaciones

| Método | Endpoint | Descripción | Query Params |
|--------|----------|-------------|--------------|
| GET | `/api/notifications` | Listar notificaciones | `read`, `type` |
| GET | `/api/notifications/{id}` | Obtener notificación | - |
| POST | `/api/notifications` | Crear notificación | - |
| PUT | `/api/notifications/{id}/read` | Marcar como leída | - |
| PUT | `/api/notifications/read-all` | Marcar todas leídas | - |
| DELETE | `/api/notifications/{id}` | Eliminar notificación | - |
| GET | `/api/notifications/stats` | Estadísticas | - |

**Tipos**: `info`, `warning`, `error`, `success`

### 📁 Carga de Archivos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/upload` | Subir archivo (imagen o documento) |

**Form Data**:
- `file`: Archivo a subir
- `type`: `image` o `document` (opcional, default: `image`)

**Límites**:
- Imágenes: 10 MB
- Documentos: 20 MB

**Formatos permitidos**:
- Imágenes: JPG, JPEG, PNG, GIF, WEBP
- Documentos: PDF, DOC, DOCX, XLS, XLSX

### 🌐 Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/public/vehicles` | Vehículos disponibles |
| GET | `/api/public/vehicles/{id}` | Detalle de vehículo |
| GET | `/api/public/advisors` | Lista de asesores activos |

## 🔒 Seguridad

### Implementado

- ✅ **JWT (JSON Web Tokens)**: Autenticación stateless
- ✅ **Tokens con expiración**: 24 horas por defecto (configurable)
- ✅ **CORS**: Configurado para permitir requests desde frontend
- ✅ **Validación de datos**: Bean Validation en todos los endpoints
- ✅ **Manejo de errores**: Respuestas estandarizadas
- ✅ **Logging de seguridad**: Registro de intentos de autenticación

### ⚠️ Estado Temporal

> **Importante**: Actualmente las contraseñas se almacenan en **texto plano** para facilitar el desarrollo inicial. La infraestructura BCrypt está preparada pero deshabilitada temporalmente.

**Antes de producción**:
- [ ] Activar encriptación BCrypt
- [ ] Migrar contraseñas existentes
- [ ] Implementar control de acceso basado en roles (RBAC)
- [ ] Añadir rate limiting en login
- [ ] Implementar refresh tokens

Para más detalles, consulta [SECURITY.md](SECURITY.md)

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

### Formato de Respuesta Estandarizado

**Respuesta Exitosa**:
```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { /* datos */ },
  "timestamp": "2025-10-21T10:30:00"
}
```

**Respuesta de Error**:
```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null,
  "timestamp": "2025-10-21T10:30:00"
}
```

### Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | Operación exitosa |
| 201 | Recurso creado exitosamente |
| 204 | Recurso eliminado exitosamente |
| 400 | Error de validación |
| 401 | No autenticado o token inválido |
| 404 | Recurso no encontrado |
| 500 | Error interno del servidor |

### Excepciones Personalizadas

- `ResourceNotFoundException`: Recurso no encontrado (404)
- `ValidationException`: Error de validación (400)
- `UnauthorizedException`: No autorizado (401)

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=UserServiceTest

# Con cobertura (JaCoCo)
mvn test jacoco:report

# Ver reporte de cobertura
# Abre: target/site/jacoco/index.html
```

### Tipos de Tests

- **Unit Tests**: Tests de servicios y utilidades
- **Integration Tests**: Tests de controladores y repositorios
- **Reactive Tests**: Usando `StepVerifier` de Project Reactor

## 📖 Documentación Adicional

- **[SECURITY.md](SECURITY.md)**: Documentación completa de seguridad
- **[CONTRIBUTING.md](CONTRIBUTING.md)**: Guía para contribuir al proyecto
- **[CHANGELOG.md](CHANGELOG.md)**: Historial de cambios y versiones

## 🤝 Contribuir

¿Quieres contribuir al proyecto? Lee nuestra [Guía de Contribución](CONTRIBUTING.md) para conocer:

- Cómo configurar el entorno de desarrollo
- Estándares de código y convenciones
- Proceso de Pull Requests
- Cómo reportar bugs

### Quick Start para Contribuir

```bash
# 1. Fork el repositorio
# 2. Clonar tu fork
git clone https://github.com/tu-usuario/hinope-backend.git

# 3. Crear branch de feature
git checkout -b feature/mi-nueva-feature

# 4. Hacer cambios y commit
git commit -m "feat: añadir nueva funcionalidad"

# 5. Push y crear Pull Request
git push origin feature/mi-nueva-feature
```

## 📝 Notas Importantes

- 🔄 **Programación Reactiva**: El proyecto usa Mono/Flux (Project Reactor)
- 🚫 **No Bloqueante**: Todas las operaciones de BD son asíncronas
- 🔐 **Seguridad**: Los passwords nunca se retornan en las respuestas
- 📁 **Archivos**: Se almacenan en `public/uploads/`
- 📊 **Logging**: Configurado con SLF4J y Logback

## 🚀 Roadmap

### Próximas Funcionalidades

- [ ] Activar encriptación BCrypt de contraseñas
- [ ] Implementar RBAC (Control de acceso basado en roles)
- [ ] Sistema de refresh tokens
- [ ] Rate limiting en endpoints de autenticación
- [ ] Recuperación de contraseña por email
- [ ] Websockets para notificaciones en tiempo real
- [ ] Auditoría de acciones del sistema
- [ ] Exportación de reportes (PDF, Excel)

## 👥 Equipo

Proyecto desarrollado para **Hino Perú**

## 📄 Licencia

Este proyecto es privado y confidencial.

---

**Versión**: 0.1.0  
**Última actualización**: Octubre 2025

Para más información, consulta la [documentación completa](CONTRIBUTING.md) o contacta al equipo de desarrollo.
