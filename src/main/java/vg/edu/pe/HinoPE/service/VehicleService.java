package vg.edu.pe.HinoPE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.entity.Vehicle;
import vg.edu.pe.HinoPE.repository.VehicleRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    
    /**
     * Get all vehicles
     */
    public Flux<Vehicle> getAllVehicles() {
        log.debug("Getting all vehicles");
        return vehicleRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Get vehicles by type
     */
    public Flux<Vehicle> getVehiclesByType(String type) {
        log.debug("Getting vehicles by type: {}", type);
        return vehicleRepository.findByTipo(type);
    }
    
    /**
     * Get vehicles by status
     */
    public Flux<Vehicle> getVehiclesByStatus(String status) {
        log.debug("Getting vehicles by status: {}", status);
        return vehicleRepository.findByEstado(status);
    }
    
    /**
     * Get vehicle by ID
     */
    public Mono<Vehicle> getVehicleById(Long id) {
        log.debug("Getting vehicle by id: {}", id);
        return vehicleRepository.findById(id);
    }
    
    /**
     * Create a new vehicle
     */
    public Mono<Vehicle> createVehicle(Vehicle vehicle) {
        log.debug("Creating vehicle: {}", vehicle.getModelo());
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * Update an existing vehicle
     */
    public Mono<Vehicle> updateVehicle(Long id, Vehicle vehicle) {
        log.debug("Updating vehicle with id: {}", id);
        return vehicleRepository.findById(id)
                .flatMap(existingVehicle -> {
                    // Update fields
                    if (vehicle.getModelo() != null) existingVehicle.setModelo(vehicle.getModelo());
                    if (vehicle.getTipo() != null) existingVehicle.setTipo(vehicle.getTipo());
                    if (vehicle.getCategoria() != null) existingVehicle.setCategoria(vehicle.getCategoria());
                    if (vehicle.getPrecio() != null) existingVehicle.setPrecio(vehicle.getPrecio());
                    if (vehicle.getCapacidad() != null) existingVehicle.setCapacidad(vehicle.getCapacidad());
                    if (vehicle.getMotor() != null) existingVehicle.setMotor(vehicle.getMotor());
                    if (vehicle.getAño() != null) existingVehicle.setAño(vehicle.getAño());
                    if (vehicle.getEstado() != null) existingVehicle.setEstado(vehicle.getEstado());
                    if (vehicle.getStock() != null) existingVehicle.setStock(vehicle.getStock());
                    if (vehicle.getImagenUrl() != null) existingVehicle.setImagenUrl(vehicle.getImagenUrl());
                    if (vehicle.getDescripcion() != null) existingVehicle.setDescripcion(vehicle.getDescripcion());
                    
                    existingVehicle.setUpdatedAt(LocalDateTime.now());
                    return vehicleRepository.save(existingVehicle);
                });
    }
    
    /**
     * Delete a vehicle
     */
    public Mono<Void> deleteVehicle(Long id) {
        log.debug("Deleting vehicle with id: {}", id);
        return vehicleRepository.deleteById(id);
    }
    
    /**
     * Get vehicle statistics
     */
    public Mono<Map<String, Object>> getVehicleStats() {
        log.debug("Getting vehicle statistics");
        
        return vehicleRepository.findAll()
                .collectList()
                .map(vehicles -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    long total = vehicles.size();
                    long disponibles = vehicles.stream()
                            .filter(v -> "disponible".equals(v.getEstado()))
                            .count();
                    long reservados = vehicles.stream()
                            .filter(v -> "reservado".equals(v.getEstado()))
                            .count();
                    long vendidos = vehicles.stream()
                            .filter(v -> "vendido".equals(v.getEstado()))
                            .count();
                    int stockTotal = vehicles.stream()
                            .mapToInt(v -> v.getStock() != null ? v.getStock() : 0)
                            .sum();
                    
                    stats.put("total", total);
                    stats.put("disponibles", disponibles);
                    stats.put("reservados", reservados);
                    stats.put("vendidos", vendidos);
                    stats.put("stockTotal", stockTotal);
                    
                    return stats;
                });
    }
}
