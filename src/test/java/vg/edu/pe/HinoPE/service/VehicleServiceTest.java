package vg.edu.pe.HinoPE.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vg.edu.pe.HinoPE.model.entity.Vehicle;
import vg.edu.pe.HinoPE.repository.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @InjectMocks
    private VehicleService vehicleService;
    
    private Vehicle testVehicle;
    
    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setModelo("Hino 300");
        testVehicle.setTipo("camion");
        testVehicle.setCategoria("Carga Ligera");
        testVehicle.setPrecio(new BigDecimal("50000.00"));
        testVehicle.setCapacidad("3.5 toneladas");
        testVehicle.setMotor("N04C-VH");
        testVehicle.setAño(2024);
        testVehicle.setEstado("disponible");
        testVehicle.setStock(5);
        testVehicle.setImagenUrl("/images/hino300.jpg");
        testVehicle.setDescripcion("Camión de carga ligera");
        testVehicle.setCreatedAt(LocalDateTime.now());
        testVehicle.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void getAllVehicles_ShouldReturnAllVehicles() {
        // Arrange
        when(vehicleRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Flux.just(testVehicle));
        
        // Act & Assert
        StepVerifier.create(vehicleService.getAllVehicles())
                .expectNext(testVehicle)
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }
    
    @Test
    void getVehiclesByType_ShouldReturnFilteredVehicles() {
        // Arrange
        when(vehicleRepository.findByTipo("camion"))
                .thenReturn(Flux.just(testVehicle));
        
        // Act & Assert
        StepVerifier.create(vehicleService.getVehiclesByType("camion"))
                .expectNext(testVehicle)
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findByTipo("camion");
    }
    
    @Test
    void getVehiclesByStatus_ShouldReturnFilteredVehicles() {
        // Arrange
        when(vehicleRepository.findByEstado("disponible"))
                .thenReturn(Flux.just(testVehicle));
        
        // Act & Assert
        StepVerifier.create(vehicleService.getVehiclesByStatus("disponible"))
                .expectNext(testVehicle)
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findByEstado("disponible");
    }
    
    @Test
    void getVehicleById_WhenExists_ShouldReturnVehicle() {
        // Arrange
        when(vehicleRepository.findById(1L))
                .thenReturn(Mono.just(testVehicle));
        
        // Act & Assert
        StepVerifier.create(vehicleService.getVehicleById(1L))
                .expectNext(testVehicle)
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findById(1L);
    }
    
    @Test
    void getVehicleById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(vehicleRepository.findById(999L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(vehicleService.getVehicleById(999L))
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findById(999L);
    }
    
    @Test
    void createVehicle_ShouldSaveAndReturnVehicle() {
        // Arrange
        Vehicle newVehicle = new Vehicle();
        newVehicle.setModelo("Hino 500");
        newVehicle.setTipo("camion");
        
        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(2L);
        savedVehicle.setModelo("Hino 500");
        savedVehicle.setTipo("camion");
        savedVehicle.setCreatedAt(LocalDateTime.now());
        savedVehicle.setUpdatedAt(LocalDateTime.now());
        
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(Mono.just(savedVehicle));
        
        // Act & Assert
        StepVerifier.create(vehicleService.createVehicle(newVehicle))
                .assertNext(vehicle -> {
                    assertThat(vehicle.getId()).isEqualTo(2L);
                    assertThat(vehicle.getModelo()).isEqualTo("Hino 500");
                    assertThat(vehicle.getCreatedAt()).isNotNull();
                    assertThat(vehicle.getUpdatedAt()).isNotNull();
                })
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
    
    @Test
    void updateVehicle_WhenExists_ShouldUpdateAndReturn() {
        // Arrange
        Vehicle updateData = new Vehicle();
        updateData.setModelo("Hino 300 Updated");
        updateData.setPrecio(new BigDecimal("55000.00"));
        
        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(1L);
        updatedVehicle.setModelo("Hino 300 Updated");
        updatedVehicle.setPrecio(new BigDecimal("55000.00"));
        updatedVehicle.setTipo("camion");
        
        when(vehicleRepository.findById(1L))
                .thenReturn(Mono.just(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(Mono.just(updatedVehicle));
        
        // Act & Assert
        StepVerifier.create(vehicleService.updateVehicle(1L, updateData))
                .assertNext(vehicle -> {
                    assertThat(vehicle.getModelo()).isEqualTo("Hino 300 Updated");
                    assertThat(vehicle.getPrecio()).isEqualTo(new BigDecimal("55000.00"));
                })
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
    
    @Test
    void updateVehicle_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        Vehicle updateData = new Vehicle();
        updateData.setModelo("Updated");
        
        when(vehicleRepository.findById(999L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(vehicleService.updateVehicle(999L, updateData))
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findById(999L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
    
    @Test
    void deleteVehicle_ShouldCallRepository() {
        // Arrange
        when(vehicleRepository.deleteById(1L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(vehicleService.deleteVehicle(1L))
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void getVehicleStats_ShouldReturnCorrectStatistics() {
        // Arrange
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setEstado("disponible");
        vehicle1.setStock(5);
        
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setEstado("reservado");
        vehicle2.setStock(3);
        
        Vehicle vehicle3 = new Vehicle();
        vehicle3.setEstado("vendido");
        vehicle3.setStock(0);
        
        when(vehicleRepository.findAll())
                .thenReturn(Flux.just(vehicle1, vehicle2, vehicle3));
        
        // Act & Assert
        StepVerifier.create(vehicleService.getVehicleStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(3L);
                    assertThat(stats.get("disponibles")).isEqualTo(1L);
                    assertThat(stats.get("reservados")).isEqualTo(1L);
                    assertThat(stats.get("vendidos")).isEqualTo(1L);
                    assertThat(stats.get("stockTotal")).isEqualTo(8);
                })
                .verifyComplete();
        
        verify(vehicleRepository, times(1)).findAll();
    }
    
    @Test
    void getVehicleStats_WithEmptyList_ShouldReturnZeroStats() {
        // Arrange
        when(vehicleRepository.findAll())
                .thenReturn(Flux.empty());
        
        // Act & Assert
        StepVerifier.create(vehicleService.getVehicleStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(0L);
                    assertThat(stats.get("disponibles")).isEqualTo(0L);
                    assertThat(stats.get("stockTotal")).isEqualTo(0);
                })
                .verifyComplete();
    }
}
