package vg.edu.pe.HinoPE.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vg.edu.pe.HinoPE.model.entity.Quote;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre del cliente es requerido")
    private String clienteNombre;
    
    @NotBlank(message = "El email del cliente es requerido")
    @Email(message = "El email debe ser válido")
    private String clienteEmail;
    
    private String clienteTelefono;
    
    private String empresa;
    
    @NotBlank(message = "El tipo de vehículo es requerido")
    private String tipoVehiculo;
    
    private String mensaje;
    
    @NotBlank(message = "El estado es requerido")
    @Pattern(regexp = "pendiente|en-proceso|enviada|cerrada", message = "El estado debe ser 'pendiente', 'en-proceso', 'enviada' o 'cerrada'")
    private String estado;
    
    @NotBlank(message = "La prioridad es requerida")
    @Pattern(regexp = "alta|media|baja", message = "La prioridad debe ser 'alta', 'media' o 'baja'")
    private String prioridad;
    
    private Long asesorAsignadoId;
    
    // Additional field for displaying advisor name (not in entity)
    private String asesorNombre;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Mapper methods
    public static QuoteDTO fromEntity(Quote quote) {
        QuoteDTO dto = new QuoteDTO();
        dto.setId(quote.getId());
        dto.setClienteNombre(quote.getClienteNombre());
        dto.setClienteEmail(quote.getClienteEmail());
        dto.setClienteTelefono(quote.getClienteTelefono());
        dto.setEmpresa(quote.getEmpresa());
        dto.setTipoVehiculo(quote.getTipoVehiculo());
        dto.setMensaje(quote.getMensaje());
        dto.setEstado(quote.getEstado());
        dto.setPrioridad(quote.getPrioridad());
        dto.setAsesorAsignadoId(quote.getAsesorAsignadoId());
        dto.setCreatedAt(quote.getCreatedAt());
        dto.setUpdatedAt(quote.getUpdatedAt());
        return dto;
    }
    
    public Quote toEntity() {
        Quote quote = new Quote();
        quote.setId(this.id);
        quote.setClienteNombre(this.clienteNombre);
        quote.setClienteEmail(this.clienteEmail);
        quote.setClienteTelefono(this.clienteTelefono);
        quote.setEmpresa(this.empresa);
        quote.setTipoVehiculo(this.tipoVehiculo);
        quote.setMensaje(this.mensaje);
        quote.setEstado(this.estado);
        quote.setPrioridad(this.prioridad);
        quote.setAsesorAsignadoId(this.asesorAsignadoId);
        quote.setCreatedAt(this.createdAt);
        quote.setUpdatedAt(this.updatedAt);
        return quote;
    }
}
