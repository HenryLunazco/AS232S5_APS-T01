package vg.edu.pe.HinoPE.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("quotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quote {
    
    @Id
    private Long id;
    
    @Column("cliente_nombre")
    private String clienteNombre;
    
    @Column("cliente_email")
    private String clienteEmail;
    
    @Column("cliente_telefono")
    private String clienteTelefono;
    
    private String empresa;
    
    @Column("tipo_vehiculo")
    private String tipoVehiculo;
    
    private String mensaje;
    
    private String estado;
    
    private String prioridad;
    
    @Column("asesor_asignado_id")
    private Long asesorAsignadoId;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
