package vg.edu.pe.HinoPE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.entity.Notification;
import vg.edu.pe.HinoPE.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    /**
     * Get all notifications
     */
    public Flux<Notification> getAllNotifications() {
        log.debug("Getting all notifications");
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Get notifications by read status
     */
    public Flux<Notification> getNotificationsByReadStatus(Boolean read) {
        log.debug("Getting notifications by read status: {}", read);
        return notificationRepository.findByLeido(read);
    }
    
    /**
     * Get notifications by type
     */
    public Flux<Notification> getNotificationsByType(String type) {
        log.debug("Getting notifications by type: {}", type);
        return notificationRepository.findByTipo(type);
    }
    
    /**
     * Get notification by ID
     */
    public Mono<Notification> getNotificationById(Long id) {
        log.debug("Getting notification by id: {}", id);
        return notificationRepository.findById(id);
    }
    
    /**
     * Create a new notification
     */
    public Mono<Notification> createNotification(Notification notification) {
        log.debug("Creating notification: {}", notification.getTitulo());
        
        // Set default values
        if (notification.getLeido() == null) {
            notification.setLeido(false);
        }
        
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark notification as read
     */
    public Mono<Notification> markAsRead(Long id) {
        log.debug("Marking notification {} as read", id);
        
        return notificationRepository.findById(id)
                .flatMap(notification -> {
                    notification.setLeido(true);
                    notification.setUpdatedAt(LocalDateTime.now());
                    return notificationRepository.save(notification);
                });
    }
    
    /**
     * Mark all notifications as read
     */
    public Mono<Void> markAllAsRead() {
        log.debug("Marking all notifications as read");
        
        return notificationRepository.findByLeido(false)
                .flatMap(notification -> {
                    notification.setLeido(true);
                    notification.setUpdatedAt(LocalDateTime.now());
                    return notificationRepository.save(notification);
                })
                .then();
    }
    
    /**
     * Delete a notification
     */
    public Mono<Void> deleteNotification(Long id) {
        log.debug("Deleting notification with id: {}", id);
        return notificationRepository.deleteById(id);
    }
    
    /**
     * Get notification statistics
     */
    public Mono<Map<String, Object>> getNotificationStats() {
        log.debug("Getting notification statistics");
        
        return notificationRepository.findAll()
                .collectList()
                .map(notifications -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    long total = notifications.size();
                    long noLeidas = notifications.stream()
                            .filter(n -> !n.getLeido())
                            .count();
                    long leidas = notifications.stream()
                            .filter(Notification::getLeido)
                            .count();
                    long alta = notifications.stream()
                            .filter(n -> "alta".equals(n.getPrioridad()))
                            .count();
                    long media = notifications.stream()
                            .filter(n -> "media".equals(n.getPrioridad()))
                            .count();
                    long baja = notifications.stream()
                            .filter(n -> "baja".equals(n.getPrioridad()))
                            .count();
                    
                    stats.put("total", total);
                    stats.put("noLeidas", noLeidas);
                    stats.put("leidas", leidas);
                    stats.put("prioridadAlta", alta);
                    stats.put("prioridadMedia", media);
                    stats.put("prioridadBaja", baja);
                    
                    return stats;
                });
    }
}
