# Guía de Contribución - HinoPE Backend

¡Gracias por tu interés en contribuir al proyecto HinoPE! Esta guía te ayudará a comenzar.

## Tabla de Contenidos

1. [Código de Conducta](#código-de-conducta)
2. [Primeros Pasos](#primeros-pasos)
3. [Configuración del Entorno](#configuración-del-entorno)
4. [Flujo de Trabajo](#flujo-de-trabajo)
5. [Estándares de Código](#estándares-de-código)
6. [Estructura del Proyecto](#estructura-del-proyecto)
7. [Testing](#testing)
8. [Documentación](#documentación)
9. [Pull Requests](#pull-requests)
10. [Reportar Bugs](#reportar-bugs)

---

## Código de Conducta

### Nuestro Compromiso

Nos comprometemos a hacer de la participación en este proyecto una experiencia libre de acoso para todos.

### Comportamiento Esperado

- Usar lenguaje acogedor e inclusivo
- Respetar diferentes puntos de vista y experiencias
- Aceptar críticas constructivas con gracia
- Enfocarse en lo que es mejor para el equipo
- Mostrar empatía hacia otros miembros

### Comportamiento Inaceptable

- Uso de lenguaje o imágenes sexualizadas
- Comentarios insultantes o despectivos
- Acoso público o privado
- Publicar información privada de otros sin permiso

---

## Primeros Pasos

### Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 17** o superior
- **Maven 3.6+**
- **Git**
- **PostgreSQL 12+** (o acceso a Neon)
- **IDE recomendado**: IntelliJ IDEA, Eclipse, o VS Code con extensiones Java

### Herramientas Recomendadas

- **Postman** o **Insomnia**: Para probar endpoints
- **DBeaver** o **pgAdmin**: Para gestión de base de datos
- **Git GUI**: GitKraken, SourceTree, o GitHub Desktop

---

## Configuración del Entorno

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-organizacion/hinope-backend.git
cd hinope-backend
```

### 2. Configurar Variables de Entorno

```bash
# Copiar el archivo de ejemplo
cp .env.example .env

# Editar con tus credenciales
# Windows: notepad .env
# Linux/Mac: nano .env
```

Variables requeridas:
```env
DB_URL=r2dbc:postgresql://localhost:5432/hinope
DB_USERNAME=postgres
DB_PASSWORD=tu_password
JWT_SECRET=clave_secreta_minimo_256_bits
JWT_EXPIRATION=86400000
SERVER_PORT=8080
```

### 3. Instalar Dependencias

```bash
mvn clean install
```

### 4. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

### 5. Verificar Instalación

Abre tu navegador en:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health Check: `http://localhost:8080/actuator/health` (si está habilitado)

---

## Flujo de Trabajo

### Branching Strategy

Usamos **Git Flow** simplificado:

```
main (producción)
  └── develop (desarrollo)
       ├── feature/nombre-feature
       ├── bugfix/nombre-bug
       └── hotfix/nombre-hotfix
```

### Crear una Nueva Feature

```bash
# 1. Actualizar develop
git checkout develop
git pull origin develop

# 2. Crear branch de feature
git checkout -b feature/nombre-descriptivo

# 3. Hacer cambios y commits
git add .
git commit -m "feat: descripción del cambio"

# 4. Push a remoto
git push origin feature/nombre-descriptivo

# 5. Crear Pull Request en GitHub
```

### Convención de Nombres de Branches

- `feature/`: Nueva funcionalidad
- `bugfix/`: Corrección de bug
- `hotfix/`: Corrección urgente en producción
- `refactor/`: Refactorización de código
- `docs/`: Cambios en documentación
- `test/`: Añadir o modificar tests

**Ejemplos**:
- `feature/user-authentication`
- `bugfix/fix-quote-validation`
- `refactor/improve-jwt-util`
- `docs/update-api-documentation`

---

## Estándares de Código

### Convención de Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>(<scope>): <descripción>

[cuerpo opcional]

[footer opcional]
```

**Tipos**:
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Formato, punto y coma faltante, etc.
- `refactor`: Refactorización de código
- `test`: Añadir o modificar tests
- `chore`: Tareas de mantenimiento

**Ejemplos**:
```bash
feat(auth): add JWT token refresh endpoint
fix(user): resolve email validation bug
docs(readme): update installation instructions
refactor(vehicle): simplify query filters
test(quote): add unit tests for QuoteService
```

### Estilo de Código Java

#### Nomenclatura

```java
// Clases: PascalCase
public class UserService { }

// Métodos y variables: camelCase
public User getUserById(Long userId) { }
private String userName;

// Constantes: UPPER_SNAKE_CASE
private static final int MAX_RETRY_ATTEMPTS = 3;

// Paquetes: lowercase
package vg.edu.pe.hinope.service;
```

#### Formato

- **Indentación**: 4 espacios (no tabs)
- **Línea máxima**: 120 caracteres
- **Llaves**: Estilo K&R (llave de apertura en la misma línea)

```java
// ✅ Correcto
public void method() {
    if (condition) {
        doSomething();
    }
}

// ❌ Incorrecto
public void method()
{
    if (condition)
    {
        doSomething();
    }
}
```

#### Anotaciones

```java
// Anotaciones de clase en líneas separadas
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    // Anotaciones de método en línea separada
    @Override
    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
```

#### Lombok

Usa Lombok para reducir boilerplate:

```java
@Data                    // Getters, setters, toString, equals, hashCode
@NoArgsConstructor       // Constructor sin argumentos
@AllArgsConstructor      // Constructor con todos los argumentos
@RequiredArgsConstructor // Constructor con campos final
@Slf4j                   // Logger
@Builder                 // Patrón Builder
```

### Programación Reactiva

#### Mono y Flux

```java
// Mono: 0 o 1 elemento
public Mono<User> getUserById(Long id) {
    return userRepository.findById(id);
}

// Flux: 0 a N elementos
public Flux<User> getAllUsers() {
    return userRepository.findAll();
}
```

#### Operadores Comunes

```java
// map: transformar elementos
userRepository.findById(id)
    .map(UserDTO::fromEntity);

// flatMap: operaciones asíncronas
userRepository.findById(id)
    .flatMap(user -> quoteRepository.findByUserId(user.getId()));

// filter: filtrar elementos
userRepository.findAll()
    .filter(user -> "admin".equals(user.getRol()));

// switchIfEmpty: valor por defecto
userRepository.fi
ndById(id)
    .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario no encontrado")));
```

### Manejo de Errores

```java
// Usar excepciones personalizadas
throw new ResourceNotFoundException("Usuario no encontrado");
throw new ValidationException("Email inválido");
throw new UnauthorizedException("Token inválido");

// Logging apropiado
log.debug("Buscando usuario con id: {}", id);
log.info("Usuario creado exitosamente: {}", user.getEmail());
log.warn("Intento de login fallido para: {}", email);
log.error("Error al procesar solicitud", exception);
```

---

## Estructura del Proyecto

```
src/main/java/vg/edu/pe/HinoPE/
├── config/                    # Configuraciones
│   ├── CorsConfig.java       # Configuración CORS
│   ├── SecurityConfig.java   # Configuración de seguridad
│   ├── R2dbcConfig.java      # Configuración R2DBC
│   └── OpenApiConfig.java    # Configuración Swagger
│
├── controller/                # Controladores REST
│   ├── AuthController.java
│   ├── UserController.java
│   ├── VehicleController.java
│   ├── QuoteController.java
│   └── NotificationController.java
│
├── service/                   # Lógica de negocio
│   ├── AuthService.java
│   ├── UserService.java
│   ├── VehicleService.java
│   └── QuoteService.java
│
├── repository/                # Acceso a datos
│   ├── UserRepository.java
│   ├── VehicleRepository.java
│   └── QuoteRepository.java
│
├── model/
│   ├── entity/               # Entidades de BD
│   │   ├── User.java
│   │   ├── Vehicle.java
│   │   └── Quote.java
│   ├── dto/                  # Data Transfer Objects
│   │   ├── UserDTO.java
│   │   ├── CreateUserRequest.java
│   │   └── ApiResponse.java
│   └── enums/                # Enumeraciones
│       ├── UserRole.java
│       └── QuoteStatus.java
│
├── security/                  # Seguridad
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityContextRepository.java
│
├── exception/                 # Excepciones
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── ValidationException.java
│
└── util/                      # Utilidades
    └── PasswordUtil.java
```

### Añadir un Nuevo Módulo

Para añadir un nuevo módulo (ej: "Clientes"):

1. **Crear Entidad**
```java
@Table("clientes")
@Data
public class Cliente {
    @Id
    private Long id;
    private String nombre;
    // ...
}
```

2. **Crear Repository**
```java
public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {
    Mono<Cliente> findByEmail(String email);
}
```

3. **Crear DTOs**
```java
@Data
public class ClienteDTO {
    private Long id;
    private String nombre;
    // ...
}
```

4. **Crear Service**
```java
@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;
    
    public Flux<Cliente> getAll() {
        return repository.findAll();
    }
}
```

5. **Crear Controller**
```java
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService service;
    
    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<ClienteDTO>>>> getAll() {
        // ...
    }
}
```

---

## Testing

### Estructura de Tests

```
src/test/java/vg/edu/pe/HinoPE/
├── service/
│   ├── UserServiceTest.java
│   └── VehicleServiceTest.java
├── controller/
│   ├── UserControllerTest.java
│   └── VehicleControllerTest.java
└── repository/
    └── UserRepositoryTest.java
```

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        
        // When
        Mono<User> result = userService.getUserById(userId);
        
        // Then
        StepVerifier.create(result)
            .expectNext(user)
            .verifyComplete();
    }
}
```

### Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void getAllUsers_ShouldReturnUserList() {
        webTestClient.get()
            .uri("/api/users")
            .header("Authorization", "Bearer " + token)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserDTO.class)
            .hasSize(3);
    }
}
```

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=UserServiceTest

# Con cobertura
mvn test jacoco:report
```

---

## Documentación

### JavaDoc

Documenta clases y métodos públicos:

```java
/**
 * Servicio para gestión de usuarios.
 * Proporciona operaciones CRUD y lógica de negocio relacionada con usuarios.
 *
 * @author Tu Nombre
 * @since 0.1.0
 */
@Service
public class UserService {
    
    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario a buscar
     * @return Mono con el usuario encontrado, o Mono.empty() si no existe
     * @throws IllegalArgumentException si el ID es null
     */
    public Mono<User> getUserById(Long id) {
        // ...
    }
}
```

### Swagger/OpenAPI

Documenta endpoints con anotaciones:

```java
@Operation(
    summary = "Obtener usuario por ID",
    description = "Retorna un usuario específico basado en su ID"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
    @ApiResponse(responseCode = "401", description = "No autenticado")
})
@GetMapping("/{id}")
public Mono<ResponseEntity<ApiResponse<UserDTO>>> getUserById(@PathVariable Long id) {
    // ...
}
```

### README y Documentación

Al añadir nuevas features, actualiza:
- `README.md`: Endpoints y ejemplos
- `SECURITY.md`: Si afecta seguridad
- `CHANGELOG.md`: Registra el cambio

---

## Pull Requests

### Checklist antes de crear PR

- [ ] El código compila sin errores
- [ ] Todos los tests pasan
- [ ] Se añadieron tests para nueva funcionalidad
- [ ] El código sigue los estándares del proyecto
- [ ] Se actualizó la documentación
- [ ] Los commits siguen Conventional Commits
- [ ] No hay credenciales o datos sensibles en el código

### Crear Pull Request

1. **Push tu branch**
```bash
git push origin feature/tu-feature
```

2. **Crear PR en GitHub**
   - Ve a la página del repositorio
   - Click en "Pull requests" > "New pull request"
   - Selecciona tu branch
   - Completa la plantilla de PR

3. **Plantilla de PR**
```markdown
## Descripción
Breve descripción de los cambios realizados.

## Tipo de cambio
- [ ] Bug fix
- [ ] Nueva feature
- [ ] Breaking change
- [ ] Documentación

## ¿Cómo se ha probado?
Describe las pruebas realizadas.

## Checklist
- [ ] Mi código sigue los estándares del proyecto
- [ ] He realizado una auto-revisión de mi código
- [ ] He comentado mi código en áreas difíciles
- [ ] He actualizado la documentación
- [ ] Mis cambios no generan nuevos warnings
- [ ] He añadido tests que prueban mi fix/feature
- [ ] Tests unitarios e integración pasan localmente
```

### Revisión de Código

Tu PR será revisado por al menos un miembro del equipo. Pueden solicitar cambios:

- Responde a los comentarios
- Realiza los cambios solicitados
- Push los cambios (se actualizará automáticamente el PR)
- Solicita nueva revisión

### Merge

Una vez aprobado:
- El PR será merged a `develop`
- Tu branch será eliminado automáticamente
- Los cambios se incluirán en el próximo release

---

## Reportar Bugs

### Antes de Reportar

1. Verifica que no sea un bug ya reportado
2. Asegúrate de estar usando la última versión
3. Recopila información del error

### Crear Issue

Usa la plantilla de bug report:

```markdown
## Descripción del Bug
Descripción clara y concisa del bug.

## Pasos para Reproducir
1. Ir a '...'
2. Click en '...'
3. Scroll hasta '...'
4. Ver error

## Comportamiento Esperado
Qué esperabas que sucediera.

## Comportamiento Actual
Qué sucedió realmente.

## Screenshots
Si aplica, añade screenshots.

## Entorno
- OS: [ej. Windows 11]
- Java Version: [ej. 17.0.2]
- Spring Boot Version: [ej. 3.5.6]

## Logs
```
Pega aquí los logs relevantes
```

## Información Adicional
Cualquier otro contexto sobre el problema.
```

---

## Preguntas Frecuentes

### ¿Cómo actualizo mi fork?

```bash
# Añadir upstream (solo una vez)
git remote add upstream https://github.com/organizacion/hinope-backend.git

# Actualizar
git fetch upstream
git checkout develop
git merge upstream/develop
git push origin develop
```

### ¿Cómo resuelvo conflictos?

```bash
# Actualizar develop
git checkout develop
git pull origin develop

# Rebase tu feature
git checkout feature/tu-feature
git rebase develop

# Resolver conflictos manualmente
# Luego:
git add .
git rebase --continue
git push origin feature/tu-feature --force
```

### ¿Dónde pido ayuda?

- **Issues**: Para bugs y features
- **Discussions**: Para preguntas generales
- **Slack/Discord**: Para chat en tiempo real
- **Email**: equipo@hinope.com

---

## Recursos Útiles

### Documentación Oficial
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [R2DBC](https://r2dbc.io/)
- [Project Reactor](https://projectreactor.io/docs)

### Tutoriales
- [Reactive Programming with Spring](https://spring.io/reactive)
- [JWT Authentication](https://jwt.io/introduction)
- [Git Flow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

### Herramientas
- [Postman Collections](./docs/postman/)
- [Database Schema](./docs/database/)
- [Architecture Diagrams](./docs/architecture/)

---

## Agradecimientos

¡Gracias por contribuir a HinoPE! Tu trabajo ayuda a mejorar el sistema para todos.

**Happy Coding! 🚀**
