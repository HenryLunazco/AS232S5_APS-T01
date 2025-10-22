package vg.edu.pe.HinoPE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.entity.User;
import vg.edu.pe.HinoPE.repository.UserRepository;
import vg.edu.pe.HinoPE.util.PasswordUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Get all users
     */
    public Flux<User> getAllUsers() {
        log.debug("Getting all users");
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    public Mono<User> getUserById(Long id) {
        log.debug("Getting user by id: {}", id);
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     */
    public Mono<User> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    /**
     * Create a new user
     */
    public Mono<User> createUser(User user) {
        log.debug("Creating user: {}", user.getEmail());
        
        // Check if email already exists
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> {
                    log.warn("User with email {} already exists", user.getEmail());
                    return Mono.error(new IllegalArgumentException("El email ya está registrado"));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // Encriptar contraseña con BCrypt
                    if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                        user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
                    }
                    
                    // Set default values
                    if (user.getFechaIngreso() == null) {
                        user.setFechaIngreso(LocalDate.now());
                    }
                    if (user.getVentas() == null) {
                        user.setVentas(0);
                    }
                    
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    
                    return userRepository.save(user);
                }))
                .cast(User.class);
    }
    
    /**
     * Update an existing user
     */
    public Mono<User> updateUser(Long id, User user) {
        log.debug("Updating user with id: {}", id);
        
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    // Check if email is being changed and if it's already taken
                    if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                        return userRepository.findByEmail(user.getEmail())
                                .flatMap(emailUser -> {
                                    log.warn("Email {} is already taken", user.getEmail());
                                    return Mono.<User>error(new IllegalArgumentException("El email ya está registrado"));
                                })
                                .switchIfEmpty(Mono.defer(() -> updateUserFields(existingUser, user)));
                    } else {
                        return updateUserFields(existingUser, user);
                    }
                });
    }
    
    private Mono<User> updateUserFields(User existingUser, User user) {
        // Update fields
        if (user.getNombre() != null) existingUser.setNombre(user.getNombre());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if (user.getTelefono() != null) existingUser.setTelefono(user.getTelefono());
        if (user.getRol() != null) existingUser.setRol(user.getRol());
        if (user.getEspecialidad() != null) existingUser.setEspecialidad(user.getEspecialidad());
        if (user.getEstado() != null) existingUser.setEstado(user.getEstado());
        if (user.getVentas() != null) existingUser.setVentas(user.getVentas());
        if (user.getFechaIngreso() != null) existingUser.setFechaIngreso(user.getFechaIngreso());
        if (user.getAvatarUrl() != null) existingUser.setAvatarUrl(user.getAvatarUrl());
        
        // Encriptar contraseña con BCrypt si se proporciona
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existingUser);
    }
    
    /**
     * Delete a user
     */
    public Mono<Void> deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        return userRepository.deleteById(id);
    }
    
    /**
     * Get user statistics
     */
    public Mono<Map<String, Object>> getUserStats() {
        log.debug("Getting user statistics");
        
        return userRepository.findAll()
                .collectList()
                .map(users -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    long total = users.size();
                    long admins = users.stream()
                            .filter(u -> "admin".equals(u.getRol()))
                            .count();
                    long asesores = users.stream()
                            .filter(u -> "asesor".equals(u.getRol()))
                            .count();
                    long mecanicos = users.stream()
                            .filter(u -> "mecanico".equals(u.getRol()))
                            .count();
                    long supervisores = users.stream()
                            .filter(u -> "supervisor".equals(u.getRol()))
                            .count();
                    long activos = users.stream()
                            .filter(u -> "activo".equals(u.getEstado()))
                            .count();
                    long inactivos = users.stream()
                            .filter(u -> "inactivo".equals(u.getEstado()))
                            .count();
                    int ventasTotales = users.stream()
                            .mapToInt(u -> u.getVentas() != null ? u.getVentas() : 0)
                            .sum();
                    
                    stats.put("total", total);
                    stats.put("admins", admins);
                    stats.put("asesores", asesores);
                    stats.put("mecanicos", mecanicos);
                    stats.put("supervisores", supervisores);
                    stats.put("activos", activos);
                    stats.put("inactivos", inactivos);
                    stats.put("ventasTotales", ventasTotales);
                    
                    return stats;
                });
    }
}
