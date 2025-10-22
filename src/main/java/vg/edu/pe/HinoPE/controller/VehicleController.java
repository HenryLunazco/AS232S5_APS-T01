package vg.edu.pe.HinoPE.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.dto.ApiResponse;
import vg.edu.pe.HinoPE.model.dto.VehicleDTO;
import vg.edu.pe.HinoPE.model.entity.Vehicle;
import vg.edu.pe.HinoPE.service.VehicleService;

import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Vehículos", description = "Gestión de vehículos (camiones y buses)")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    @GetMapping
    public Flux<VehicleDTO> getAllVehicles(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        
        log.info("GET /api/vehicles - type: {}, status: {}", type, status);
        
        Flux<Vehicle> vehicles;
        
        if (type != null && !type.isEmpty()) {
            vehicles = vehicleService.getVehiclesByType(type);
        } else if (status != null && !status.isEmpty()) {
            vehicles = vehicleService.getVehiclesByStatus(status);
        } else {
            vehicles = vehicleService.getAllVehicles();
        }
        
        return vehicles.map(VehicleDTO::fromEntity);
    }
    
    @GetMapping("/{id}")
    public Mono<VehicleDTO> getVehicleById(@PathVariable Long id) {
        log.info("GET /api/vehicles/{}", id);
        return vehicleService.getVehicleById(id)
                .map(VehicleDTO::fromEntity);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<VehicleDTO>> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        log.info("POST /api/vehicles - Creating vehicle: {}", vehicleDTO.getModelo());
        
        Vehicle vehicle = vehicleDTO.toEntity();
        
        return vehicleService.createVehicle(vehicle)
                .map(VehicleDTO::fromEntity)
                .map(dto -> ApiResponse.success("Vehículo creado exitosamente", dto));
    }
    
    @PutMapping("/{id}")
    public Mono<ApiResponse<VehicleDTO>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        
        log.info("PUT /api/vehicles/{} - Updating vehicle", id);
        
        Vehicle vehicle = vehicleDTO.toEntity();
        
        return vehicleService.updateVehicle(id, vehicle)
                .map(VehicleDTO::fromEntity)
                .map(dto -> ApiResponse.success("Vehículo actualizado exitosamente", dto));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteVehicle(@PathVariable Long id) {
        log.info("DELETE /api/vehicles/{}", id);
        return vehicleService.deleteVehicle(id);
    }
    
    @GetMapping("/stats")
    public Mono<Map<String, Object>> getVehicleStats() {
        log.info("GET /api/vehicles/stats");
        return vehicleService.getVehicleStats();
    }
}
