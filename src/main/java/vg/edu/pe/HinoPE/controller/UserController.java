package vg.edu.pe.HinoPE.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.dto.ApiResponse;
import vg.edu.pe.HinoPE.model.dto.UserDTO;
import vg.edu.pe.HinoPE.model.entity.User;
import vg.edu.pe.HinoPE.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema (admins, asesores, drivers)")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public Flux<UserDTO> getAllUsers() {
        log.info("GET /api/users");
        return userService.getAllUsers()
                .map(UserDTO::fromEntity);
    }
    
    @GetMapping("/{id}")
    public Mono<UserDTO> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        return userService.getUserById(id)
                .map(UserDTO::fromEntity);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("POST /api/users - Creating user: {}", userDTO.getEmail());
        
        User user = userDTO.toEntity();
        // Set password hash from password field
        if (userDTO.getPassword() != null) {
            user.setPasswordHash(userDTO.getPassword());
        }
        
        return userService.createUser(user)
                .map(UserDTO::fromEntity)
                .map(dto -> ApiResponse.success("Usuario creado exitosamente", dto));
    }
    
    @PutMapping("/{id}")
    public Mono<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        
        log.info("PUT /api/users/{} - Updating user", id);
        
        User user = userDTO.toEntity();
        // Set password hash from password field if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(userDTO.getPassword());
        }
        
        return userService.updateUser(id, user)
                .map(UserDTO::fromEntity)
                .map(dto -> ApiResponse.success("Usuario actualizado exitosamente", dto));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);
        return userService.deleteUser(id);
    }
    
    @GetMapping("/stats")
    public Mono<Map<String, Object>> getUserStats() {
        log.info("GET /api/users/stats");
        return userService.getUserStats();
    }
}
