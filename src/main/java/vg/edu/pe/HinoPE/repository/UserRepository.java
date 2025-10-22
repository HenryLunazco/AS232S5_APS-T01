package vg.edu.pe.HinoPE.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.entity.User;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    /**
     * Find user by email (for authentication)
     */
    Mono<User> findByEmail(String email);

    /**
     * Find all users by role (admin, asesor, driver)
     * Using CAST to handle PostgreSQL ENUM type
     */
    @Query("SELECT * FROM users WHERE rol::text = $1")
    Flux<User> findByRol(String rol);

    /**
     * Find all users by status (activo, inactivo)
     * Using CAST to handle PostgreSQL ENUM type
     */
    @Query("SELECT * FROM users WHERE estado::text = $1")
    Flux<User> findByEstado(String estado);
}
