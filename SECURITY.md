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
| `mecanico` | Mecánico del taller | Mantenimiento y gestión de vehículos |
| `supervisor` | Supervisor de operaciones | Control y supervisión de procesos |

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

### ✅ Estado Actual

**Las contraseñas están encriptadas con BCrypt**. Se hashean al crear/actualizar usuarios y se verifican durante el login.

#### Implementación Actual

**AuthService.java**:
```java
// Verificación con BCrypt
boolean matches = PasswordUtil.verifyPassword(password, user.getPasswordHash());
```

**UserService.java**:
```java
// Hasheo de contraseña al crear/actualizar usuarios
if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
    user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
}
```

### Implementación de BCrypt

#### PasswordUtil
- **Ubicación**: `src/main/java/vg/edu/pe/HinoPE/util/PasswordUtil.java`
- **Algoritmo**: BCrypt con factor de trabajo 10
- **Métodos**:
  - `hashPassword(String plainPassword)`: Hashea una contraseña
  - `verifyPassword(String plainPassword, String hashedPassword)`: Verifica una contraseña

#### Características de Seguridad

- **Algoritmo**: BCrypt con factor de trabajo 10
- **Salt**: Generado automáticamente por BCrypt
- **Hash**: 60 caracteres en formato `$2a$10$...`
- **Resistente a**: Rainbow tables, fuerza bruta
- **Verificación**: Tiempo constante para prevenir timing attacks

#### Migración de Contraseñas

Si tienes contraseñas en texto plano en la base de datos, puedes usar este script SQL:

```sql
-- Habilitar extensión pgcrypto
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Encriptar contraseñas existentes
UPDATE users 
SET password_hash = crypt(password_hash, gen_salt('bf'))
WHERE password_hash NOT LIKE '$2%';
```

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
7. **Encriptación BCrypt**: Contraseñas hasheadas con factor 10
8. **Verificación Segura**: Comparación de contraseñas con timing constante

### Recomendaciones Pendientes ⚠️

1. **RBAC**: Control de acceso basado en roles a nivel de endpoint
2. **Rate Limiting**: Limitar intentos de login (prevenir fuerza bruta)
3. **Refresh Tokens**: Implementar tokens de refresco
4. **Token Blacklist**: Lista negra para tokens revocados
5. **Auditoría**: Registro de acciones de seguridad críticas
6. **2FA**: Autenticación de dos factores (opcional)
7. **Password Policy**: Políticas de contraseñas fuertes (longitud, complejidad)
8. **Account Lockout**: Bloqueo tras intentos fallidos

---

## Estado Actual y Pendientes

### ✅ Implementado

- [x] Autenticación JWT
- [x] Generación y validación de tokens
- [x] Filtro de autenticación reactivo
- [x] Endpoints públicos y protegidos
- [x] Extracción de información del usuario desde token
- [x] Manejo de errores de autenticación
- [x] Documentación Swagger con autenticación JWT
- [x] Encriptación BCrypt de contraseñas
- [x] Hasheo automático al crear/actualizar usuarios
- [x] Verificación segura de contraseñas en login

### ⚠️ Pendiente

- [ ] Control de acceso basado en roles (RBAC) a nivel de endpoint
- [ ] Políticas de contraseñas fuertes (longitud mínima, complejidad)

### 🔜 Por Implementar

- [ ] Control de acceso basado en roles (RBAC) a nivel de endpoint
- [ ] Refresh tokens para renovación de sesión
- [ ] Rate limiting en login (prevenir fuerza bruta)
- [ ] Token blacklist/revocación
- [ ] Auditoría de seguridad (log de acciones críticas)
- [ ] Políticas de contraseñas (longitud mínima, complejidad)
- [ ] Recuperación de contraseña por email
- [ ] Cambio de contraseña para usuarios
- [ ] Bloqueo de cuenta tras intentos fallidos
- [ ] Autenticación de dos factores (2FA)

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
