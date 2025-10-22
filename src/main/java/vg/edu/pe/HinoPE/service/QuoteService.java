package vg.edu.pe.HinoPE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.entity.Quote;
import vg.edu.pe.HinoPE.repository.QuoteRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteService {
    
    private final QuoteRepository quoteRepository;
    
    /**
     * Get all quotes
     */
    public Flux<Quote> getAllQuotes() {
        log.debug("Getting all quotes");
        return quoteRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Get quotes by status
     */
    public Flux<Quote> getQuotesByStatus(String status) {
        log.debug("Getting quotes by status: {}", status);
        return quoteRepository.findByEstado(status);
    }
    
    /**
     * Get quotes by priority
     */
    public Flux<Quote> getQuotesByPriority(String priority) {
        log.debug("Getting quotes by priority: {}", priority);
        return quoteRepository.findByPrioridad(priority);
    }
    
    /**
     * Get quote by ID
     */
    public Mono<Quote> getQuoteById(Long id) {
        log.debug("Getting quote by id: {}", id);
        return quoteRepository.findById(id);
    }
    
    /**
     * Create a new quote
     */
    public Mono<Quote> createQuote(Quote quote) {
        log.debug("Creating quote for client: {}", quote.getClienteNombre());
        
        // Set default status if not provided
        if (quote.getEstado() == null || quote.getEstado().isEmpty()) {
            quote.setEstado("pendiente");
        }
        
        // Set default priority if not provided
        if (quote.getPrioridad() == null || quote.getPrioridad().isEmpty()) {
            quote.setPrioridad("media");
        }
        
        quote.setCreatedAt(LocalDateTime.now());
        quote.setUpdatedAt(LocalDateTime.now());
        
        return quoteRepository.save(quote);
    }
    
    /**
     * Update an existing quote
     */
    public Mono<Quote> updateQuote(Long id, Quote quote) {
        log.debug("Updating quote with id: {}", id);
        
        return quoteRepository.findById(id)
                .flatMap(existingQuote -> {
                    // Update fields
                    if (quote.getClienteNombre() != null) existingQuote.setClienteNombre(quote.getClienteNombre());
                    if (quote.getClienteEmail() != null) existingQuote.setClienteEmail(quote.getClienteEmail());
                    if (quote.getClienteTelefono() != null) existingQuote.setClienteTelefono(quote.getClienteTelefono());
                    if (quote.getEmpresa() != null) existingQuote.setEmpresa(quote.getEmpresa());
                    if (quote.getTipoVehiculo() != null) existingQuote.setTipoVehiculo(quote.getTipoVehiculo());
                    if (quote.getMensaje() != null) existingQuote.setMensaje(quote.getMensaje());
                    if (quote.getEstado() != null) existingQuote.setEstado(quote.getEstado());
                    if (quote.getPrioridad() != null) existingQuote.setPrioridad(quote.getPrioridad());
                    if (quote.getAsesorAsignadoId() != null) existingQuote.setAsesorAsignadoId(quote.getAsesorAsignadoId());
                    
                    existingQuote.setUpdatedAt(LocalDateTime.now());
                    return quoteRepository.save(existingQuote);
                });
    }
    
    /**
     * Assign advisor to quote
     */
    public Mono<Quote> assignAdvisor(Long quoteId, Long advisorId) {
        log.debug("Assigning advisor {} to quote {}", advisorId, quoteId);
        
        return quoteRepository.findById(quoteId)
                .flatMap(quote -> {
                    quote.setAsesorAsignadoId(advisorId);
                    quote.setUpdatedAt(LocalDateTime.now());
                    return quoteRepository.save(quote);
                });
    }
    
    /**
     * Delete a quote
     */
    public Mono<Void> deleteQuote(Long id) {
        log.debug("Deleting quote with id: {}", id);
        return quoteRepository.deleteById(id);
    }
    
    /**
     * Get quote statistics
     */
    public Mono<Map<String, Object>> getQuoteStats() {
        log.debug("Getting quote statistics");
        
        return quoteRepository.findAll()
                .collectList()
                .map(quotes -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    long total = quotes.size();
                    long pendientes = quotes.stream()
                            .filter(q -> "pendiente".equals(q.getEstado()))
                            .count();
                    long enProceso = quotes.stream()
                            .filter(q -> "en-proceso".equals(q.getEstado()))
                            .count();
                    long enviadas = quotes.stream()
                            .filter(q -> "enviada".equals(q.getEstado()))
                            .count();
                    long cerradas = quotes.stream()
                            .filter(q -> "cerrada".equals(q.getEstado()))
                            .count();
                    long alta = quotes.stream()
                            .filter(q -> "alta".equals(q.getPrioridad()))
                            .count();
                    long media = quotes.stream()
                            .filter(q -> "media".equals(q.getPrioridad()))
                            .count();
                    long baja = quotes.stream()
                            .filter(q -> "baja".equals(q.getPrioridad()))
                            .count();
                    
                    stats.put("total", total);
                    stats.put("pendientes", pendientes);
                    stats.put("enProceso", enProceso);
                    stats.put("enviadas", enviadas);
                    stats.put("cerradas", cerradas);
                    stats.put("prioridadAlta", alta);
                    stats.put("prioridadMedia", media);
                    stats.put("prioridadBaja", baja);
                    
                    return stats;
                });
    }
}
