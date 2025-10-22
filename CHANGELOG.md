# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [Unreleased]

### Por Implementar
- Encriptación BCrypt de contraseñas
- Control de acceso basado en roles (RBAC)
- Sistema de refresh tokens
- Rate limiting en endpoints de autenticación
- Recuperación de contraseña por email
- Auditoría de acciones de seguridad

---

## [0.1.0] - 2025-10-21

### Añadido

#### Infraestructura
- Configuración inicial del proyecto Spring Boot 3.5.6
- Integración con PostgreSQL mediante R2DBC (acceso reactivo)
- Configuración de variables de entorno con spring-dotenv
- Estructura de carpetas modular (config, controller, service, repository, model, security, util)
- Configuración de CORS para permitir requests desde frontend
- Documentación OpenAPI/Swagger UI

#### Autenticación y Seguridad
- Sistema de autenticación JWT con Spring Security
- Generación y validación de tokens JWT (HS256)
- Filtro de autenticación reactivo (`JwtAuthenticationFilter`)
- Repositorio de contexto de seguridad (`SecurityContextRepository`)
- Utilidad para manejo de JWT (`JwtUtil`)
- Infraestructura BCrypt preparada para hasheo de contraseñas
- Endpoints públicos y protegidos configurados
- Manejo de errores de autenticación (401 Unauthorized)

#### Módulo de Usuarios
- CRUD completo de usuarios
- Modelo de entidad `User` con R2DBC
- DTOs para transferencia de datos (`UserDTO`, `CreateUserRequest`, `UpdateUserRequest`)
- Repositorio reactivo `UserRepository`
- Servicio `UserService` con lógica de negocio
- Controlador `UserController` con endpoints REST
- Endpoint de estadísticas de usuarios
- Validación de email único
- Roles de usuario: admin, asesor, driver

#### Módulo de Vehículos
- CRUD completo de vehículos
- Modelo de entidad `Vehicle` con R2DBC
- DTOs para transferencia de datos (`VehicleDTO`, `CreateVehicleRequest`, `UpdateVehicleRequest`)
- Repositorio reactivo `VehicleRepository`
- Servicio `VehicleService` con lógica de negocio
- Controlador `VehicleController` con endpoints REST
- Filtros por tipo y estado
- Endpoint de estadísticas de vehículos
- Endpoints públicos para consulta sin autenticación

#### Módulo de Cotizaciones
- CRUD completo de cotizaciones
- Modelo de entidad `Quote` con R2DBC
- DTOs para transferencia de datos (`QuoteDTO`, `CreateQuoteRequest`, `UpdateQuoteRequest`)
- Repositorio reactivo `QuoteRepository`
- Servicio `QuoteService` con lógica de negocio
- Controlador `QuoteController` con endpoints REST
- Asignación de asesores a cotizaciones
- Filtros por estado y prioridad
- Endpoint de estadísticas de cotizaciones
- Estados: pendiente, en_proceso, aprobada, rechazada
- Prioridades: baja, media, alta, urgente

#### Módulo de Notificaciones
- CRUD completo de notificaciones
- Modelo de entidad `Notification` con R2DBC
- DTOs para transferencia de datos (`NotificationDTO`, `CreateNotificationRequest`)
- Repositorio reactivo `NotificationRepository`
- Servicio `NotificationService` con lógica de negocio
- Controlador `NotificationController` con endpoints REST
- Marcar notificaciones como leídas (individual y masivo)
- Filtros por estado de lectura y tipo
- Endpoint de estadísticas de notificaciones
- Tipos: info, warning, error, success

#### Módulo de Archivos
- Servicio de almacenamiento de archivos (`FileStorageService`)
- Controlador de carga de archivos (`UploadController`)
- Soporte para imágenes y documentos
- Validación de tipos de archivo permitidos
- Validación de tamaño máximo configurable
- Generación de nombres únicos con UUID
- Almacenamiento en carpetas separadas (images/documents)

#### Manejo de Errores
- Manejador global de excepciones (`GlobalExceptionHandler`)
- Excepciones personalizadas:
  - `ResourceNotFoundException` (404)
  - `UnauthorizedException` (401)
  - `ValidationException` (400)
- Formato de respuesta estandarizado (`ApiResponse<T>`)
- Logging de errores con SLF4J

#### Documentación
- README.md completo con guía de uso
- SECURITY.md con documentación de seguridad
- Documentación de API con Swagger/OpenAPI
- Ejemplos de requests y responses
- Guía de configuración de variables de entorno

### Configurado

#### Dependencias
- Spring Boot 3.5.6
- Spring WebFlux (programación reactiva)
- Spring Data R2DBC (acceso reactivo a PostgreSQL)
- Spring Security (autenticación y autorización)
- PostgreSQL R2DBC Driver
- JWT (jjwt 0.11.5)
- Lombok (reducción de boilerplate)
- Spring Validation (validación de datos)
- SpringDoc OpenAPI (documentación Swagger)
- Spring Dotenv (variables de entorno)

#### Base de Datos
- Conexión R2DBC a PostgreSQL
- Pool de conexiones configurado
- Logging de queries SQL (configurable)
- Soporte para SSL (Neon PostgreSQL)

#### Logging
- Configuración de niveles de log por paquete
- Logging de queries R2DBC
- Logging de parámetros SQL
- Logging de eventos de seguridad

### Temporal

#### ⚠️ Configuración de Desarrollo
- Contraseñas almacenadas en texto plano (sin BCrypt)
- Comparación directa de contraseñas en login
- Sin control de acceso basado en roles a nivel de endpoint
- Todos los usuarios autenticados tienen acceso a todos los recursos

> **Nota**: Esta configuración es temporal para facilitar el desarrollo inicial.
> Debe activarse BCrypt y RBAC antes de producción.

---

## Tipos de Cambios

- **Añadido**: para nuevas funcionalidades
- **Cambiado**: para cambios en funcionalidades existentes
- **Obsoleto**: para funcionalidades que serán eliminadas
- **Eliminado**: para funcionalidades eliminadas
- **Corregido**: para corrección de errores
- **Seguridad**: para vulnerabilidades de seguridad
- **Temporal**: para configuraciones temporales de desarrollo

---

## Versionado

Este proyecto usa [Semantic Versioning](https://semver.org/lang/es/):

- **MAJOR** (X.0.0): Cambios incompatibles con versiones anteriores
- **MINOR** (0.X.0): Nueva funcionalidad compatible con versiones anteriores
- **PATCH** (0.0.X): Correcciones de errores compatibles con versiones anteriores

---

## Enlaces

- [Repositorio](https://github.com/HenryLunazco/AS232S5_APS-T01.git)
- [Documentación](README.md)
- [Seguridad](SECURITY.md)
- [Contribuir](CONTRIBUTING.md)
