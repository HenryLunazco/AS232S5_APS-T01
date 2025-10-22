package vg.edu.pe.HinoPE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.dto.LoginResponse;
import vg.edu.pe.HinoPE.model.dto.UserDTO;
import vg.edu.pe.HinoPE.model.entity.User;
import vg.edu.pe.HinoPE.repository.UserRepository;
import vg.edu.pe.HinoPE.security.JwtUtil;
/* import vg.edu.pe.HinoPE.util.PasswordUtil;*/

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Authenticate user with email and password
     * TEMPORAL: Acepta contraseñas en texto plano (sin encriptar)
     */
    public Mono<LoginResponse> authenticate(String email, String password) {
        log.debug("Authenticating user: {}", email);

        return userRepository.findByEmail(email)
                .filter(user -> {
                    // TEMPORAL: Comparación directa sin hash
                    boolean matches = password.equals(user.getPasswordHash());
                    if (!matches) {
                        log.warn("Invalid password for user: {}", email);
                    }
                    return matches;
                })
                .map(user -> {
                    String token = generateToken(user);
                    UserDTO userDTO = UserDTO.fromEntity(user);
                    log.info("User authenticated successfully: {}", email);
                    return new LoginResponse(token, userDTO);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Authentication failed for user: {}", email);
                    return Mono.empty();
                }));
    }

    /**
     * Generate JWT token for user
     */
    public String generateToken(User user) {
        return jwtUtil.generateToken(user);
    }

    /**
     * Validate token and return user
     */
    public Mono<User> validateToken(String token) {
        try {
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                return userRepository.findByEmail(email);
            }
            return Mono.empty();
        } catch (Exception e) {
            log.error("Error validating token", e);
            return Mono.empty();
        }
    }
}
