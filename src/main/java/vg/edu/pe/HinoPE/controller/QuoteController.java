package vg.edu.pe.HinoPE.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.dto.ApiResponse;
import vg.edu.pe.HinoPE.model.dto.QuoteDTO;
import vg.edu.pe.HinoPE.model.entity.Quote;
import vg.edu.pe.HinoPE.service.QuoteService;

import java.util.Map;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Cotizaciones", description = "Gestión de cotizaciones de clientes")
public class QuoteController {
    
    private final QuoteService quoteService;
    
    @GetMapping
    public Flux<QuoteDTO> getAllQuotes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        
        log.info("GET /api/quotes - status: {}, priority: {}", status, priority);
        
        Flux<Quote> quotes;
        
        if (status != null && !status.isEmpty()) {
            quotes = quoteService.getQuotesByStatus(status);
        } else if (priority != null && !priority.isEmpty()) {
            quotes = quoteService.getQuotesByPriority(priority);
        } else {
            quotes = quoteService.getAllQuotes();
        }
        
        return quotes.map(QuoteDTO::fromEntity);
    }
    
    @GetMapping("/{id}")
    public Mono<QuoteDTO> getQuoteById(@PathVariable Long id) {
        log.info("GET /api/quotes/{}", id);
        return quoteService.getQuoteById(id)
                .map(QuoteDTO::fromEntity);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<QuoteDTO>> createQuote(@Valid @RequestBody QuoteDTO quoteDTO) {
        log.info("POST /api/quotes - Creating quote for: {}", quoteDTO.getClienteNombre());
        
        Quote quote = quoteDTO.toEntity();
        
        return quoteService.createQuote(quote)
                .map(QuoteDTO::fromEntity)
                .map(dto -> ApiResponse.success("Cotización creada exitosamente", dto));
    }
    
    @PutMapping("/{id}")
    public Mono<ApiResponse<QuoteDTO>> updateQuote(
            @PathVariable Long id,
            @Valid @RequestBody QuoteDTO quoteDTO) {
        
        log.info("PUT /api/quotes/{} - Updating quote", id);
        
        Quote quote = quoteDTO.toEntity();
        
        return quoteService.updateQuote(id, quote)
                .map(QuoteDTO::fromEntity)
                .map(dto -> ApiResponse.success("Cotización actualizada exitosamente", dto));
    }
    
    @PutMapping("/{id}/assign")
    public Mono<ApiResponse<QuoteDTO>> assignAdvisor(
            @PathVariable Long id,
            @RequestParam Long advisorId) {
        
        log.info("PUT /api/quotes/{}/assign - Assigning advisor: {}", id, advisorId);
        
        return quoteService.assignAdvisor(id, advisorId)
                .map(QuoteDTO::fromEntity)
                .map(dto -> ApiResponse.success("Asesor asignado exitosamente", dto));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteQuote(@PathVariable Long id) {
        log.info("DELETE /api/quotes/{}", id);
        return quoteService.deleteQuote(id);
    }
    
    @GetMapping("/stats")
    public Mono<Map<String, Object>> getQuoteStats() {
        log.info("GET /api/quotes/stats");
        return quoteService.getQuoteStats();
    }
}
