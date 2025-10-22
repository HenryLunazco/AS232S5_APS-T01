package vg.edu.pe.HinoPE.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vg.edu.pe.HinoPE.model.entity.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    
    private Long id;
    
    @NotBlank(message = "El modelo es requerido")
    private String modelo;
    
    @NotBlank(message = "El tipo es requerido")
    @Pattern(regexp = "camion|bus", message = "El tipo debe ser 'camion' o 'bus'")
    private String tipo;
    
    @NotBlank(message = "La categoría es requerida")
    private String categoria;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @NotBlank(message = "La capacidad es requerida")
    private String capacidad;
    
    @NotBlank(message = "El motor es requerido")
    private String motor;
    
    @NotNull(message = "El año es requerido")
    @Min(value = 2000, message = "El año debe ser mayor o igual a 2000")
    @Max(value = 2100, message = "El año debe ser menor o igual a 2100")
    private Integer año;
    
    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "disponible|reservado|vendido", message = "El estado debe ser 'disponible', 'reservado' o 'vendido'")
    private String estado;
    
    @NotNull(message = "El stock es requerido")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;
    
    private String imagenUrl;
    
    private String descripcion;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Mapper methods
    public static VehicleDTO fromEntity(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setModelo(vehicle.getModelo());
        dto.setTipo(vehicle.getTipo());
        dto.setCategoria(vehicle.getCategoria());
        dto.setPrecio(vehicle.getPrecio());
        dto.setCapacidad(vehicle.getCapacidad());
        dto.setMotor(vehicle.getMotor());
        dto.setAño(vehicle.getAño());
        dto.setEstado(vehicle.getEstado());
        dto.setStock(vehicle.getStock());
        dto.setImagenUrl(vehicle.getImagenUrl());
        dto.setDescripcion(vehicle.getDescripcion());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        return dto;
    }
    
    public Vehicle toEntity() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(this.id);
        vehicle.setModelo(this.modelo);
        vehicle.setTipo(this.tipo);
        vehicle.setCategoria(this.categoria);
        vehicle.setPrecio(this.precio);
        vehicle.setCapacidad(this.capacidad);
        vehicle.setMotor(this.motor);
        vehicle.setAño(this.año);
        vehicle.setEstado(this.estado);
        vehicle.setStock(this.stock);
        vehicle.setImagenUrl(this.imagenUrl);
        vehicle.setDescripcion(this.descripcion);
        vehicle.setCreatedAt(this.createdAt);
        vehicle.setUpdatedAt(this.updatedAt);
        return vehicle;
    }
}
