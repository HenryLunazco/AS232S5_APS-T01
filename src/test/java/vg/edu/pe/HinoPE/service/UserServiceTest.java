package vg.edu.pe.HinoPE.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vg.edu.pe.HinoPE.model.entity.User;
import vg.edu.pe.HinoPE.repository.UserRepository;
import vg.edu.pe.HinoPE.util.PasswordUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
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
        testUser.setAvatarUrl("/avatars/juan.jpg");
        testUser.setPasswordHash("$2a$10$hashedpassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        when(userRepository.findAll())
                .thenReturn(Flux.just(testUser));
        
        // Act & Assert
        StepVerifier.create(userService.getAllUsers())
                .expectNext(testUser)
                .verifyComplete();
        
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L))
                .thenReturn(Mono.just(testUser));
        
        // Act & Assert
        StepVerifier.create(userService.getUserById(1L))
                .expectNext(testUser)
                .verifyComplete();
        
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void getUserById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findById(999L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(userService.getUserById(999L))
                .verifyComplete();
        
        verify(userRepository, times(1)).findById(999L);
    }
    
    @Test
    void getUserByEmail_WhenExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("juan@example.com"))
                .thenReturn(Mono.just(testUser));
        
        // Act & Assert
        StepVerifier.create(userService.getUserByEmail("juan@example.com"))
                .expectNext(testUser)
                .verifyComplete();
        
        verify(userRepository, times(1)).findByEmail("juan@example.com");
    }
    
    @Test
    void createUser_WithValidData_ShouldHashPasswordAndSave() {
        // Arrange
        User newUser = new User();
        newUser.setNombre("María López");
        newUser.setEmail("maria@example.com");
        newUser.setPasswordHash("plainPassword");
        
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setNombre("María López");
        savedUser.setEmail("maria@example.com");
        savedUser.setPasswordHash("$2a$10$hashedpassword");
        savedUser.setVentas(0);
        savedUser.setFechaIngreso(LocalDate.now());
        
        when(userRepository.findByEmail("maria@example.com"))
                .thenReturn(Mono.empty());
        
        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.hashPassword("plainPassword"))
                    .thenReturn("$2a$10$hashedpassword");
            
            when(userRepository.save(any(User.class)))
                    .thenReturn(Mono.just(savedUser));
            
            // Act & Assert
            StepVerifier.create(userService.createUser(newUser))
                    .assertNext(user -> {
                        assertThat(user.getId()).isEqualTo(2L);
                        assertThat(user.getEmail()).isEqualTo("maria@example.com");
                        assertThat(user.getPasswordHash()).isEqualTo("$2a$10$hashedpassword");
                        assertThat(user.getVentas()).isEqualTo(0);
                    })
                    .verifyComplete();
        }
        
        verify(userRepository, times(1)).findByEmail("maria@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void createUser_WithDuplicateEmail_ShouldReturnError() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("juan@example.com");
        
        when(userRepository.findByEmail("juan@example.com"))
                .thenReturn(Mono.just(testUser));
        
        // Act & Assert
        StepVerifier.create(userService.createUser(newUser))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("El email ya está registrado"))
                .verify();
        
        verify(userRepository, times(1)).findByEmail("juan@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updateUser_WhenExists_ShouldUpdateAndReturn() {
        // Arrange
        User updateData = new User();
        updateData.setNombre("Juan Pérez Updated");
        updateData.setVentas(15);
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setNombre("Juan Pérez Updated");
        updatedUser.setEmail("juan@example.com");
        updatedUser.setVentas(15);
        
        when(userRepository.findById(1L))
                .thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(updatedUser));
        
        // Act & Assert
        StepVerifier.create(userService.updateUser(1L, updateData))
                .assertNext(user -> {
                    assertThat(user.getNombre()).isEqualTo("Juan Pérez Updated");
                    assertThat(user.getVentas()).isEqualTo(15);
                })
                .verifyComplete();
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void updateUser_WithNewEmail_WhenEmailTaken_ShouldReturnError() {
        // Arrange
        User updateData = new User();
        updateData.setEmail("taken@example.com");
        
        User existingUserWithEmail = new User();
        existingUserWithEmail.setId(2L);
        existingUserWithEmail.setEmail("taken@example.com");
        
        when(userRepository.findById(1L))
                .thenReturn(Mono.just(testUser));
        when(userRepository.findByEmail("taken@example.com"))
                .thenReturn(Mono.just(existingUserWithEmail));
        
        // Act & Assert
        StepVerifier.create(userService.updateUser(1L, updateData))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("El email ya está registrado"))
                .verify();
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updateUser_WithNewPassword_ShouldHashPassword() {
        // Arrange
        User updateData = new User();
        updateData.setPasswordHash("newPassword");
        
        when(userRepository.findById(1L))
                .thenReturn(Mono.just(testUser));
        
        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.hashPassword("newPassword"))
                    .thenReturn("$2a$10$newhashedpassword");
            
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
            
            // Act & Assert
            StepVerifier.create(userService.updateUser(1L, updateData))
                    .assertNext(user -> {
                        assertThat(user.getPasswordHash()).isEqualTo("$2a$10$newhashedpassword");
                    })
                    .verifyComplete();
        }
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void updateUser_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        User updateData = new User();
        updateData.setNombre("Updated");
        
        when(userRepository.findById(999L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(userService.updateUser(999L, updateData))
                .verifyComplete();
        
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void deleteUser_ShouldCallRepository() {
        // Arrange
        when(userRepository.deleteById(1L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(userService.deleteUser(1L))
                .verifyComplete();
        
        verify(userRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void getUserStats_ShouldReturnCorrectStatistics() {
        // Arrange
        User admin = new User();
        admin.setRol("admin");
        admin.setEstado("activo");
        admin.setVentas(5);
        
        User asesor = new User();
        asesor.setRol("asesor");
        asesor.setEstado("activo");
        asesor.setVentas(10);
        
        User driver = new User();
        driver.setRol("driver");
        driver.setEstado("inactivo");
        driver.setVentas(0);
        
        when(userRepository.findAll())
                .thenReturn(Flux.just(admin, asesor, driver));
        
        // Act & Assert
        StepVerifier.create(userService.getUserStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(3L);
                    assertThat(stats.get("admins")).isEqualTo(1L);
                    assertThat(stats.get("asesores")).isEqualTo(1L);
                    assertThat(stats.get("drivers")).isEqualTo(1L);
                    assertThat(stats.get("activos")).isEqualTo(2L);
                    assertThat(stats.get("inactivos")).isEqualTo(1L);
                    assertThat(stats.get("ventasTotales")).isEqualTo(15);
                })
                .verifyComplete();
        
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void getUserStats_WithEmptyList_ShouldReturnZeroStats() {
        // Arrange
        when(userRepository.findAll())
                .thenReturn(Flux.empty());
        
        // Act & Assert
        StepVerifier.create(userService.getUserStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(0L);
                    assertThat(stats.get("admins")).isEqualTo(0L);
                    assertThat(stats.get("ventasTotales")).isEqualTo(0);
                })
                .verifyComplete();
    }
}
