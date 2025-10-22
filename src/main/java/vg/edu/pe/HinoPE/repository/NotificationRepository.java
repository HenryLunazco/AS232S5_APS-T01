package vg.edu.pe.HinoPE.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vg.edu.pe.HinoPE.model.entity.Notification;

@Repository
public interface NotificationRepository extends R2dbcRepository<Notification, Long> {
    
    /**
     * Find all notifications by read status
     */
    Flux<Notification> findByLeido(Boolean leido);
    
    /**
     * Find all notifications by type (alert, maintenance, fuel, system, quote, user, vehicle, sale)
     * Using CAST to handle PostgreSQL ENUM type
     */
    @Query("SELECT * FROM notifications WHERE tipo::text = $1 ORDER BY created_at DESC")
    Flux<Notification> findByTipo(String tipo);
    
    /**
     * Find all notifications ordered by creation date descending
     */
    Flux<Notification> findAllByOrderByCreatedAtDesc();
}
