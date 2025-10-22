package vg.edu.pe.HinoPE.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.dto.ApiResponse;
import vg.edu.pe.HinoPE.model.dto.NotificationDTO;
import vg.edu.pe.HinoPE.model.entity.Notification;
import vg.edu.pe.HinoPE.service.NotificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Notificaciones", description = "Gestión de notificaciones del sistema")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    public Flux<NotificationDTO> getAllNotifications(
            @RequestParam(required = false) Boolean read,
            @RequestParam(required = false) String type) {
        
        log.info("GET /api/notifications - read: {}, type: {}", read, type);
        
        Flux<Notification> notifications;
        
        if (read != null) {
            notifications = notificationService.getNotificationsByReadStatus(read);
        } else if (type != null && !type.isEmpty()) {
            notifications = notificationService.getNotificationsByType(type);
        } else {
            notifications = notificationService.getAllNotifications();
        }
        
        return notifications.map(NotificationDTO::fromEntity);
    }
    
    @GetMapping("/{id}")
    public Mono<NotificationDTO> getNotificationById(@PathVariable Long id) {
        log.info("GET /api/notifications/{}", id);
        return notificationService.getNotificationById(id)
                .map(NotificationDTO::fromEntity);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<NotificationDTO>> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        log.info("POST /api/notifications - Creating notification: {}", notificationDTO.getTitulo());
        
        Notification notification = notificationDTO.toEntity();
        
        return notificationService.createNotification(notification)
                .map(NotificationDTO::fromEntity)
                .map(dto -> ApiResponse.success("Notificación creada exitosamente", dto));
    }
    
    @PutMapping("/{id}/read")
    public Mono<ApiResponse<NotificationDTO>> markAsRead(@PathVariable Long id) {
        log.info("PUT /api/notifications/{}/read", id);
        
        return notificationService.markAsRead(id)
                .map(NotificationDTO::fromEntity)
                .map(dto -> ApiResponse.success("Notificación marcada como leída", dto));
    }
    
    @PutMapping("/read-all")
    public Mono<ApiResponse<Void>> markAllAsRead() {
        log.info("PUT /api/notifications/read-all");
        
        return notificationService.markAllAsRead()
                .then(Mono.just(ApiResponse.success("Todas las notificaciones marcadas como leídas", null)));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteNotification(@PathVariable Long id) {
        log.info("DELETE /api/notifications/{}", id);
        return notificationService.deleteNotification(id);
    }
    
    @GetMapping("/stats")
    public Mono<Map<String, Object>> getNotificationStats() {
        log.info("GET /api/notifications/stats");
        return notificationService.getNotificationStats();
    }
}
