package vg.edu.pe.HinoPE.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    private Long id;
    
    private String modelo;
    
    private String tipo;
    
    private String categoria;
    
    private BigDecimal precio;
    
    private String capacidad;
    
    private String motor;
    
    @Column("año")
    private Integer año;
    
    private String estado;
    
    private Integer stock;
    
    @Column("imagen_url")
    private String imagenUrl;
    
    private String descripcion;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
