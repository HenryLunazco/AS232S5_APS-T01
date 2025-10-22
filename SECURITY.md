# Documentación de Seguridad - HinoPE

## Índice
1. [Resumen General](#resumen-general)
2. [Autenticación](#autenticación)
3. [Autorización](#autorización)
4. [Gestión de Contraseñas](#gestión-de-contraseñas)
5. [Tokens JWT](#tokens-jwt)
6. [Configuración de Seguridad](#configuración-de-seguridad)
7. [Endpoints Públicos y Protegidos](#endpoints-públicos-y-protegidos)
8. [Mejores Prácticas](#mejores-prácticas)
9. [Estado Actual y Pendientes](#estado-actual-y-pendientes)

---

## Resumen General

HinoPE implementa un sistema de seguridad basado en **JWT (JSON Web Tokens)** para autenticación stateless en una arquitectura reactiva con Spring WebFlux. El sistema utiliza Spring Security para proteger los endpoints de la API.

### Stack de Seguridad
- **Framework**: Spring Security 6.x
- **Autenticación**: JWT (JSON Web Tokens)
- **Algoritmo JWT**: HS256 (HMAC-SHA256)
- **Librería JWT**: jjwt 0.11.5
- **Encriptación de Contraseñas**: BCrypt (temporalmente deshabilitado)
- **Arquitectura**: Reactiva (WebFlux)

---

## Autenticación

### Flujo de Autenticación

1. **Login del Usuario**
   ```
   POST /api/auth/login
   {
     "email": "usuario@ejemplo.com",
     "password": "contraseña"
   }
   ```

2. **Validación de Credenciales**
   - El sistema busca al usuario por email
   - Compara la contraseña (actualmente en texto plano)
   - Si es válido, genera un token JWT

3. **Respuesta Exitosa**
   ```json
   {
     "success": true,
     "message": "Autenticación exitosa",
     "data": {
       "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
       "user": {
         "id": 1,
         "nombre": "Usuario",
         "email": "usuario@ejemplo.com",
         "rol": "admin"
       }
     }
   }
   ```

4. **Uso del Token**
   - El cliente debe incluir el token en todas las peticiones protegidas
   - Header: `Authorization: Bearer <token>`

### Componentes de Autenticación

#### AuthService
- **Ubicación**: `src/main/java/vg/edu/pe/HinoPE/service/AuthService.java`
- **Responsabilidades**:
  - Validar credenciales de usuario
  - Generar tokens JWT
  - Validar tokens existentes

#### AuthController
- **Ubicación**: `src/main/java/vg/edu/pe/HinoPE/controller/AuthController.java`
- **Endpoint**: `POST /api/auth/login`
- **Validación**: Usa `@Valid` para validar el request body

---

## Autorización

### Roles de Usuario

El sistema maneja tres roles principales:

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| `admin` | Administrador del sistema | Acceso completo a todos los recursos |
| `asesor` | Asesor de ventas | Gestión de cotizaciones y clientes |
| `driver` | Conductor | Acceso limitado a información de vehículos |

### Control de Acceso

Actualmente, el sistema valida que el usuario esté autenticado pero **no implementa control de acceso basado en roles (RBAC)** a nivel de endpoint.

**Implementación Actual**:
```java
.pathMatchers("/api/**").authenticated()
```

**Recomendación Futura**:
```java
.pathMatchers("/api/admin/**").hasRole("ADMIN")
.pathMatchers("/api/quotes/**").hasAnyRole("ADMIN", "ASESOR")
.pathMatchers("/api/vehicles/**").hasAnyRole("ADMIN", "ASESOR", "DRIVER")
```

---

## Gestión de Contraseñas

### ⚠️ Estado Actual (TEMPORAL)

**Las contraseñas actualmente NO están encriptadas**. Se almacenan y comparan en texto plano.

#### Implementación Temporal

**AuthService.java**:
```java
// Comparación directa sin hash
boolean matches = password.equals(user.getPasswordHash());
```

**UserService.java**:
```java
// No se hashea la contraseña al crear/actualizar usuarios
// Código de hasheo comentado temporalmente
```

### Implementación de BCrypt (Preparada)

El sistema tiene preparada la infraestructura para usar BCrypt:

#### PasswordUtil
- **Ubicación**: `src/main/java/vg/edu/pe/HinoPE/util/PasswordUtil.java`
- **Algoritmo**: BCrypt con factor de trabajo 10
- **Métodos**:
  - `hashPassword(String plainPassword)`: Hashea una contraseña
  - `verifyPassword(String plainPassword, String hashedPassword)`: Verifica una contraseña

#### Activar Encriptación

Para activar el hasheo de contraseñas:

1. **En AuthService.java**, cambiar:
```java
// De:
boolean matches = password.equals(user.getPasswordHash());

// A:
boolean matches = PasswordUtil.verifyPassword(password, user.getPasswordHash());
```

2. **En UserService.java**, descomentar:
```java
if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
    user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
}
```

3. **Migrar contraseñas existentes** en la base de datos a formato BCrypt

---

## Tokens JWT

### Configuración

Los tokens JWT se configuran mediante variables de entorno:

```properties
# .env
JWT_SECRET=tu_clave_secreta_muy_segura_de_al_menos_256_bits
JWT_EXPIRATION=86400000  # 24 horas en milisegundos
```

### Estructura del Token

#### Header
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### Payload (Claims)
```json
{
  "id": 1,
  "email": "usuario@ejemplo.com",
  "rol": "admin",
  "nombre": "Usuario Ejemplo",
  "sub": "usuario@ejemplo.com",
  "iat": 1234567890,
  "exp": 1234654290
}
```

#### Signature
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### JwtUtil

**Ubicación**: `src/main/java/vg/edu/pe/HinoPE/security/JwtUtil.java`

**Métodos principales**:
- `generateToken(User user)`: Genera un nuevo token
- `validateToken(String token)`: Valida si un token es válido
- `extractEmail(String token)`: Extrae el email del usuario
- `extractUserId(String token)`: Extrae el ID del usuario
- `extractUserRole(String token)`: Extrae el rol del usuario
- `isTokenExpired(String token)`: Verifica si el token expiró

### Tiempo de Expiración

- **Predeterminado**: 24 horas (86400000 ms)
- **Configurable**: Mediante variable `JWT_EXPIRATION`
- **Recomendación**: 
  - Tokens de acceso: 15-60 minutos
  - Tokens de refresco: 7-30 días (no implementado)

---

## Configuración de Seguridad

### SecurityConfig

**Ubicación**: `src/main/java/vg/edu/pe/HinoPE/config/SecurityConfig.java`

#### Características Deshabilitadas
```java
.csrf(ServerHttpSecurity.CsrfSpec::disable)      // CSRF deshabilitado (API REST)
.formLogin(ServerHttpSecurity.FormLoginSpec::disable)  // Login por formulario deshabilitado
.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)  // HTTP Basic deshabilitado
```

#### Filtros de Seguridad

1. **JwtAuthenticationFilter**
   - Intercepta todas las peticiones
   - Extrae y valida el token JWT
   - Establece el contexto de seguridad

2. **SecurityContextRepository**
   - Carga el contexto de seguridad desde el token
   - Implementación stateless (no guarda sesiones)

### JwtAuthenticationFilter

**Ubicación**: `src/main/java/vg/edu/pe/HinoPE/security/JwtAuthenticationFilter.java`

**Flujo**:
1. Verifica si la ruta es pública (skip autenticación)
2. Extrae el header `Authorization`
3. Valida el formato `Bearer <token>`
4. Valida el token con `JwtUtil`
5. Extrae información del usuario (email, rol, ID)
6. Crea objeto `Authentication`
7. Establece el contexto de seguridad reactivo

**Respuestas**:
- Token válido: Continúa con la petición
- Token inválido/ausente: HTTP 401 Unauthorized

---

## Endpoints Públicos y Protegidos

### Endpoints Públicos (Sin Autenticación)

```
POST   /api/auth/login          # Login de usuarios
GET    /api/public/**           # Recursos públicos
GET    /v3/api-docs/**          # Documentación OpenAPI
GET    /swagger-ui/**           # Interfaz Swagger
GET    /swagger-ui.html         # Página principal Swagger
GET    /webjars/**              # Recursos estáticos Swagger
```

### Endpoints Protegidos (Requieren JWT)

```
# Usuarios
GET    /api/users               # Listar usuarios
GET    /api/users/{id}          # Obtener usuario
POST   /api/users               # Crear usuario
PUT    /api/users/{id}          # Actualizar usuario
DELETE /api/users/{id}          # Eliminar usuario
GET    /api/users/stats         # Estadísticas de usuarios

# Vehículos
GET    /api/vehicles            # Listar vehículos
GET    /api/vehicles/{id}       # Obtener vehículo
POST   /api/vehicles            # Crear vehículo
PUT    /api/vehicles/{id}       # Actualizar vehículo
DELETE /api/vehicles/{id}       # Eliminar vehículo

# Cotizaciones
GET    /api/quotes              # Listar cotizaciones
GET    /api/quotes/{id}         # Obtener cotización
POST   /api/quotes              # Crear cotización
PUT    /api/quotes/{id}         # Actualizar cotización
DELETE /api/quotes/{id}         # Eliminar cotización

# Notificaciones
GET    /api/notifications       # Listar notificaciones
POST   /api/notifications       # Crear notificación
PUT    /api/notifications/{id}  # Actualizar notificación
DELETE /api/notifications/{id}  # Eliminar notificación

# Archivos
POST   /api/upload              # Subir archivo
```

---

## Mejores Prácticas

### Implementadas ✅

1. **Tokens Stateless**: No se almacenan sesiones en el servidor
2. **HTTPS Ready**: La aplicación está preparada para usar HTTPS
3. **CORS Configurado**: Control de orígenes cruzados
4. **Validación de Entrada**: Uso de `@Valid` en controllers
5. **Logging de Seguridad**: Registro de intentos de autenticación
6. **Separación de Responsabilidades**: Filtros, servicios y configuración separados

### Recomendaciones Pendientes ⚠️

1. **Activar BCrypt**: Implementar hasheo de contraseñas
2. **RBAC**: Control de acceso basado en roles
3. **Rate Limiting**: Limitar intentos de login
4. **Refresh Tokens**: Implementar tokens de refresco
5. **Token Blacklist**: Lista negra para tokens revocados
6. **Auditoría**: Registro de acciones de seguridad
7. **2FA**: Autenticación de dos factores (opcional)
8. **Password Policy**: Políticas de contraseñas fuertes

---

## Estado Actual y Pendientes

### ✅ Implementado

- [x] Autenticación JWT
- [x] Generación y validación de tokens
- [x] Filtro de autenticación reactivo
- [x] Endpoints públicos y protegidos
- [x] Extracción de información del usuario desde token
- [x] Manejo de errores de autenticación
- [x] Documentación Swagger sin autenticación
- [x] Infraestructura BCrypt preparada

### ⚠️ Temporal

- [ ] Contraseñas en texto plano (DEBE CAMBIARSE)
- [ ] Sin control de acceso por roles

### 🔜 Por Implementar

- [ ] Activar encriptación BCrypt
- [ ] Control de acceso basado en roles (RBAC)
- [ ] Refresh tokens
- [ ] Rate limiting en login
- [ ] Token blacklist/revocación
- [ ] Auditoría de seguridad
- [ ] Políticas de contraseñas
- [ ] Recuperación de contraseña
- [ ] Cambio de contraseña
- [ ] Bloqueo de cuenta tras intentos fallidos

---

## Configuración de Variables de Entorno

```properties
# JWT Configuration
JWT_SECRET=clave_secreta_minimo_256_bits_para_hs256_muy_segura
JWT_EXPIRATION=86400000

# Database (R2DBC)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hinope
DB_USER=postgres
DB_PASSWORD=password

# Server
SERVER_PORT=8080
```

### Generación de JWT_SECRET Seguro

```bash
# Opción 1: OpenSSL
openssl rand -base64 32

# Opción 2: Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"

# Opción 3: Python
python -c "import secrets; print(secrets.token_urlsafe(32))"
```

---

## Contacto y Soporte

Para reportar vulnerabilidades de seguridad, contactar al equipo de desarrollo de forma privada.

**Última actualización**: Octubre 2025
