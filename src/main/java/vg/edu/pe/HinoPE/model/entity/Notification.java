package vg.edu.pe.HinoPE.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    private Long id;
    
    private String tipo;
    
    private String prioridad;
    
    private String titulo;
    
    private String mensaje;
    
    @Column("vehiculo_id")
    private Long vehiculoId;
    
    @Column("quote_id")
    private Long quoteId;
    
    @Column("user_id")
    private Long userId;
    
    private Boolean leido;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
