package vg.edu.pe.HinoPE.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vg.edu.pe.HinoPE.model.entity.Vehicle;

@Repository
public interface VehicleRepository extends R2dbcRepository<Vehicle, Long> {
    
    /**
     * Find all vehicles by type (camion or bus)
     * Using CAST to handle PostgreSQL ENUM type
     */
    @Query("SELECT * FROM vehicles WHERE tipo::text = $1 ORDER BY created_at DESC")
    Flux<Vehicle> findByTipo(String tipo);
    
    /**
     * Find all vehicles by status (disponible, reservado, vendido)
     * Using CAST to handle PostgreSQL ENUM type
     */
    @Query("SELECT * FROM vehicles WHERE estado::text = $1 ORDER BY created_at DESC")
    Flux<Vehicle> findByEstado(String estado);
    
    /**
     * Find all vehicles ordered by creation date descending
     */
    Flux<Vehicle> findAllByOrderByCreatedAtDesc();
}
