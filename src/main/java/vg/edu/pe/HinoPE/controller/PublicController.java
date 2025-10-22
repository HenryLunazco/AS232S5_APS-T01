package vg.edu.pe.HinoPE.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.dto.UserDTO;
import vg.edu.pe.HinoPE.model.dto.VehicleDTO;
import vg.edu.pe.HinoPE.model.entity.Vehicle;
import vg.edu.pe.HinoPE.service.UserService;
import vg.edu.pe.HinoPE.service.VehicleService;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Público", description = "Endpoints públicos sin autenticación")
@io.swagger.v3.oas.annotations.security.SecurityRequirements() // No requiere autenticación
public class PublicController {
    
    private final VehicleService vehicleService;
    private final UserService userService;
    
    @GetMapping("/vehicles")
    public Flux<VehicleDTO> getPublicVehicles(@RequestParam(required = false) String type) {
        log.info("GET /api/public/vehicles - type: {}", type);
        
        Flux<Vehicle> vehicles;
        
        if (type != null && !type.isEmpty()) {
            vehicles = vehicleService.getVehiclesByType(type);
        } else {
            vehicles = vehicleService.getVehiclesByStatus("disponible");
        }
        
        return vehicles.map(VehicleDTO::fromEntity);
    }
    
    @GetMapping("/vehicles/{id}")
    public Mono<VehicleDTO> getPublicVehicleById(@PathVariable Long id) {
        log.info("GET /api/public/vehicles/{}", id);
        return vehicleService.getVehicleById(id)
                .map(VehicleDTO::fromEntity);
    }
    
    @GetMapping("/advisors")
    public Flux<UserDTO> getPublicAdvisors() {
        log.info("GET /api/public/advisors");
        return userService.getAllUsers()
                .filter(user -> "asesor".equals(user.getRol()) && "activo".equals(user.getEstado()))
                .map(UserDTO::fromEntity);
    }
}
