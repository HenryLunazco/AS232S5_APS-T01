package vg.edu.pe.HinoPE.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vg.edu.pe.HinoPE.model.entity.User;
import vg.edu.pe.HinoPE.repository.UserRepository;
import vg.edu.pe.HinoPE.security.JwtUtil;
import vg.edu.pe.HinoPE.util.PasswordUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setNombre("Juan Pérez");
        testUser.setEmail("juan@example.com");
        testUser.setTelefono("987654321");
        testUser.setRol("asesor");
        testUser.setEspecialidad("Camiones");
        testUser.setEstado("activo");
        testUser.setVentas(10);
        testUser.setFechaIngreso(LocalDate.now());
        testUser.setPasswordHash("$2a$10$hashedpassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void authenticate_WithValidCredentials_ShouldReturnLoginResponse() {
        // Arrange
        String email = "juan@example.com";
        String password = "password123";
        String token = "jwt.token.here";
        
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(testUser));
        
        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.verifyPassword(password, testUser.getPasswordHash()))
                    .thenReturn(true);
            
            when(jwtUtil.generateToken(testUser))
                    .thenReturn(token);
            
            // Act & Assert
            StepVerifier.create(authService.authenticate(email, password))
                    .assertNext(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getToken()).isEqualTo(token);
                        assertThat(response.getUser()).isNotNull();
                        assertThat(response.getUser().getEmail()).isEqualTo(email);
                        assertThat(response.getUser().getPassword()).isNull(); // Should not include password
                    })
                    .verifyComplete();
        }
        
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtUtil, times(1)).generateToken(testUser);
    }
    
    @Test
    void authenticate_WithInvalidPassword_ShouldReturnEmpty() {
        // Arrange
        String email = "juan@example.com";
        String password = "wrongpassword";
        
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(testUser));
        
        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.verifyPassword(password, testUser.getPasswordHash()))
                    .thenReturn(false);
            
            // Act & Assert
            StepVerifier.create(authService.authenticate(email, password))
                    .verifyComplete();
        }
        
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtUtil, never()).generateToken(any());
    }
    
    @Test
    void authenticate_WithNonExistentEmail_ShouldReturnEmpty() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";
        
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(authService.authenticate(email, password))
                .verifyComplete();
        
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtUtil, never()).generateToken(any());
    }
    
    @Test
    void generateToken_ShouldCallJwtUtil() {
        // Arrange
        String expectedToken = "jwt.token.here";
        when(jwtUtil.generateToken(testUser))
                .thenReturn(expectedToken);
        
        // Act
        String token = authService.generateToken(testUser);
        
        // Assert
        assertThat(token).isEqualTo(expectedToken);
        verify(jwtUtil, times(1)).generateToken(testUser);
    }
    
    @Test
    void validateToken_WithValidToken_ShouldReturnUser() {
        // Arrange
        String token = "valid.jwt.token";
        String email = "juan@example.com";
        
        when(jwtUtil.validateToken(token))
                .thenReturn(true);
        when(jwtUtil.extractEmail(token))
                .thenReturn(email);
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.just(testUser));
        
        // Act & Assert
        StepVerifier.create(authService.validateToken(token))
                .expectNext(testUser)
                .verifyComplete();
        
        verify(jwtUtil, times(1)).validateToken(token);
        verify(jwtUtil, times(1)).extractEmail(token);
        verify(userRepository, times(1)).findByEmail(email);
    }
    
    @Test
    void validateToken_WithInvalidToken_ShouldReturnEmpty() {
        // Arrange
        String token = "invalid.jwt.token";
        
        when(jwtUtil.validateToken(token))
                .thenReturn(false);
        
        // Act & Assert
        StepVerifier.create(authService.validateToken(token))
                .verifyComplete();
        
        verify(jwtUtil, times(1)).validateToken(token);
        verify(jwtUtil, never()).extractEmail(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }
    
    @Test
    void validateToken_WhenExceptionThrown_ShouldReturnEmpty() {
        // Arrange
        String token = "malformed.token";
        
        when(jwtUtil.validateToken(token))
                .thenThrow(new RuntimeException("Token malformed"));
        
        // Act & Assert
        StepVerifier.create(authService.validateToken(token))
                .verifyComplete();
        
        verify(jwtUtil, times(1)).validateToken(token);
    }
    
    @Test
    void validateToken_WhenUserNotFound_ShouldReturnEmpty() {
        // Arrange
        String token = "valid.jwt.token";
        String email = "deleted@example.com";
        
        when(jwtUtil.validateToken(token))
                .thenReturn(true);
        when(jwtUtil.extractEmail(token))
                .thenReturn(email);
        when(userRepository.findByEmail(email))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(authService.validateToken(token))
                .verifyComplete();
        
        verify(jwtUtil, times(1)).validateToken(token);
        verify(jwtUtil, times(1)).extractEmail(token);
        verify(userRepository, times(1)).findByEmail(email);
    }
}
