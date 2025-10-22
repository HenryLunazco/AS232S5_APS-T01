package vg.edu.pe.HinoPE.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vg.edu.pe.HinoPE.model.entity.Notification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    private Long id;
    
    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "alert|maintenance|fuel|system|quote|user|vehicle|sale", 
             message = "El tipo debe ser 'alert', 'maintenance', 'fuel', 'system', 'quote', 'user', 'vehicle' o 'sale'")
    private String tipo;
    
    @NotBlank(message = "La prioridad es requerida")
    @Pattern(regexp = "alta|media|baja", message = "La prioridad debe ser 'alta', 'media' o 'baja'")
    private String prioridad;
    
    @NotBlank(message = "El título es requerido")
    private String titulo;
    
    @NotBlank(message = "El mensaje es requerido")
    private String mensaje;
    
    private Long vehiculoId;
    
    private Long quoteId;
    
    private Long userId;
    
    private Boolean leido;
    
    // Additional fields for displaying related entity names (not in entity)
    private String vehiculoModelo;
    
    private String quoteCliente;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Mapper methods
    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTipo(notification.getTipo());
        dto.setPrioridad(notification.getPrioridad());
        dto.setTitulo(notification.getTitulo());
        dto.setMensaje(notification.getMensaje());
        dto.setVehiculoId(notification.getVehiculoId());
        dto.setQuoteId(notification.getQuoteId());
        dto.setUserId(notification.getUserId());
        dto.setLeido(notification.getLeido());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }
    
    public Notification toEntity() {
        Notification notification = new Notification();
        notification.setId(this.id);
        notification.setTipo(this.tipo);
        notification.setPrioridad(this.prioridad);
        notification.setTitulo(this.titulo);
        notification.setMensaje(this.mensaje);
        notification.setVehiculoId(this.vehiculoId);
        notification.setQuoteId(this.quoteId);
        notification.setUserId(this.userId);
        notification.setLeido(this.leido != null ? this.leido : false);
        notification.setCreatedAt(this.createdAt);
        notification.setUpdatedAt(this.updatedAt);
        return notification;
    }
}
