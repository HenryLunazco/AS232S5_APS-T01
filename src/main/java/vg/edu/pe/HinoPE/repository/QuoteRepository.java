package vg.edu.pe.HinoPE.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vg.edu.pe.HinoPE.model.entity.Quote;

@Repository
public interface QuoteRepository extends R2dbcRepository<Quote, Long> {
    
    /**
     * Find all quotes by status (pendiente, en-proceso, enviada, cerrada)
     * Using CAST to handle PostgreSQL ENUM type
     */
    @Query("SELECT * FROM quotes WHERE estado::text = $1 ORDER BY created_at DESC")
    Flux<Quote> findByEstado(String estado);
    
    /**
     * Find all quotes by priority (alta, media, baja)
     * Using CAST to handle PostgreSQL ENUM type
     */
    @Query("SELECT * FROM quotes WHERE prioridad::text = $1 ORDER BY created_at DESC")
    Flux<Quote> findByPrioridad(String prioridad);
    
    /**
     * Find all quotes assigned to a specific advisor
     */
    Flux<Quote> findByAsesorAsignadoId(Long asesorId);
    
    /**
     * Find all quotes ordered by creation date descending
     */
    Flux<Quote> findAllByOrderByCreatedAtDesc();
}
