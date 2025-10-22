package vg.edu.pe.HinoPE.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vg.edu.pe.HinoPE.model.entity.Quote;
import vg.edu.pe.HinoPE.repository.QuoteRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {
    
    @Mock
    private QuoteRepository quoteRepository;
    
    @InjectMocks
    private QuoteService quoteService;
    
    private Quote testQuote;
    
    @BeforeEach
    void setUp() {
        testQuote = new Quote();
        testQuote.setId(1L);
        testQuote.setClienteNombre("Carlos Ruiz");
        testQuote.setClienteEmail("carlos@example.com");
        testQuote.setClienteTelefono("987654321");
        testQuote.setEmpresa("Transportes ABC");
        testQuote.setTipoVehiculo("camion");
        testQuote.setMensaje("Necesito cotización para camión de carga");
        testQuote.setEstado("pendiente");
        testQuote.setPrioridad("media");
        testQuote.setAsesorAsignadoId(1L);
        testQuote.setCreatedAt(LocalDateTime.now());
        testQuote.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void getAllQuotes_ShouldReturnAllQuotes() {
        // Arrange
        when(quoteRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Flux.just(testQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.getAllQuotes())
                .expectNext(testQuote)
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }
    
    @Test
    void getQuotesByStatus_ShouldReturnFilteredQuotes() {
        // Arrange
        when(quoteRepository.findByEstado("pendiente"))
                .thenReturn(Flux.just(testQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.getQuotesByStatus("pendiente"))
                .expectNext(testQuote)
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findByEstado("pendiente");
    }
    
    @Test
    void getQuotesByPriority_ShouldReturnFilteredQuotes() {
        // Arrange
        when(quoteRepository.findByPrioridad("alta"))
                .thenReturn(Flux.just(testQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.getQuotesByPriority("alta"))
                .expectNext(testQuote)
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findByPrioridad("alta");
    }
    
    @Test
    void getQuoteById_WhenExists_ShouldReturnQuote() {
        // Arrange
        when(quoteRepository.findById(1L))
                .thenReturn(Mono.just(testQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.getQuoteById(1L))
                .expectNext(testQuote)
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findById(1L);
    }
    
    @Test
    void getQuoteById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(quoteRepository.findById(999L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(quoteService.getQuoteById(999L))
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findById(999L);
    }
    
    @Test
    void createQuote_WithDefaultValues_ShouldSetDefaults() {
        // Arrange
        Quote newQuote = new Quote();
        newQuote.setClienteNombre("María López");
        newQuote.setClienteEmail("maria@example.com");
        
        Quote savedQuote = new Quote();
        savedQuote.setId(2L);
        savedQuote.setClienteNombre("María López");
        savedQuote.setClienteEmail("maria@example.com");
        savedQuote.setEstado("pendiente");
        savedQuote.setPrioridad("media");
        savedQuote.setCreatedAt(LocalDateTime.now());
        savedQuote.setUpdatedAt(LocalDateTime.now());
        
        when(quoteRepository.save(any(Quote.class)))
                .thenReturn(Mono.just(savedQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.createQuote(newQuote))
                .assertNext(quote -> {
                    assertThat(quote.getEstado()).isEqualTo("pendiente");
                    assertThat(quote.getPrioridad()).isEqualTo("media");
                    assertThat(quote.getCreatedAt()).isNotNull();
                })
                .verifyComplete();
        
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }
    
    @Test
    void createQuote_WithProvidedValues_ShouldKeepValues() {
        // Arrange
        Quote newQuote = new Quote();
        newQuote.setClienteNombre("Pedro Sánchez");
        newQuote.setEstado("en-proceso");
        newQuote.setPrioridad("alta");
        
        Quote savedQuote = new Quote();
        savedQuote.setId(3L);
        savedQuote.setClienteNombre("Pedro Sánchez");
        savedQuote.setEstado("en-proceso");
        savedQuote.setPrioridad("alta");
        
        when(quoteRepository.save(any(Quote.class)))
                .thenReturn(Mono.just(savedQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.createQuote(newQuote))
                .assertNext(quote -> {
                    assertThat(quote.getEstado()).isEqualTo("en-proceso");
                    assertThat(quote.getPrioridad()).isEqualTo("alta");
                })
                .verifyComplete();
    }
    
    @Test
    void updateQuote_WhenExists_ShouldUpdateAndReturn() {
        // Arrange
        Quote updateData = new Quote();
        updateData.setEstado("enviada");
        updateData.setPrioridad("alta");
        
        Quote updatedQuote = new Quote();
        updatedQuote.setId(1L);
        updatedQuote.setClienteNombre("Carlos Ruiz");
        updatedQuote.setEstado("enviada");
        updatedQuote.setPrioridad("alta");
        
        when(quoteRepository.findById(1L))
                .thenReturn(Mono.just(testQuote));
        when(quoteRepository.save(any(Quote.class)))
                .thenReturn(Mono.just(updatedQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.updateQuote(1L, updateData))
                .assertNext(quote -> {
                    assertThat(quote.getEstado()).isEqualTo("enviada");
                    assertThat(quote.getPrioridad()).isEqualTo("alta");
                })
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findById(1L);
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }
    
    @Test
    void updateQuote_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        Quote updateData = new Quote();
        updateData.setEstado("enviada");
        
        when(quoteRepository.findById(999L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(quoteService.updateQuote(999L, updateData))
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findById(999L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }
    
    @Test
    void assignAdvisor_WhenQuoteExists_ShouldAssignAndReturn() {
        // Arrange
        Long quoteId = 1L;
        Long advisorId = 5L;
        
        Quote updatedQuote = new Quote();
        updatedQuote.setId(quoteId);
        updatedQuote.setAsesorAsignadoId(advisorId);
        
        when(quoteRepository.findById(quoteId))
                .thenReturn(Mono.just(testQuote));
        when(quoteRepository.save(any(Quote.class)))
                .thenReturn(Mono.just(updatedQuote));
        
        // Act & Assert
        StepVerifier.create(quoteService.assignAdvisor(quoteId, advisorId))
                .assertNext(quote -> {
                    assertThat(quote.getAsesorAsignadoId()).isEqualTo(advisorId);
                })
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findById(quoteId);
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }
    
    @Test
    void assignAdvisor_WhenQuoteNotExists_ShouldReturnEmpty() {
        // Arrange
        when(quoteRepository.findById(999L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(quoteService.assignAdvisor(999L, 5L))
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findById(999L);
        verify(quoteRepository, never()).save(any(Quote.class));
    }
    
    @Test
    void deleteQuote_ShouldCallRepository() {
        // Arrange
        when(quoteRepository.deleteById(1L))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(quoteService.deleteQuote(1L))
                .verifyComplete();
        
        verify(quoteRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void getQuoteStats_ShouldReturnCorrectStatistics() {
        // Arrange
        Quote quote1 = new Quote();
        quote1.setEstado("pendiente");
        quote1.setPrioridad("alta");
        
        Quote quote2 = new Quote();
        quote2.setEstado("en-proceso");
        quote2.setPrioridad("media");
        
        Quote quote3 = new Quote();
        quote3.setEstado("enviada");
        quote3.setPrioridad("baja");
        
        Quote quote4 = new Quote();
        quote4.setEstado("cerrada");
        quote4.setPrioridad("alta");
        
        when(quoteRepository.findAll())
                .thenReturn(Flux.just(quote1, quote2, quote3, quote4));
        
        // Act & Assert
        StepVerifier.create(quoteService.getQuoteStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(4L);
                    assertThat(stats.get("pendientes")).isEqualTo(1L);
                    assertThat(stats.get("enProceso")).isEqualTo(1L);
                    assertThat(stats.get("enviadas")).isEqualTo(1L);
                    assertThat(stats.get("cerradas")).isEqualTo(1L);
                    assertThat(stats.get("prioridadAlta")).isEqualTo(2L);
                    assertThat(stats.get("prioridadMedia")).isEqualTo(1L);
                    assertThat(stats.get("prioridadBaja")).isEqualTo(1L);
                })
                .verifyComplete();
        
        verify(quoteRepository, times(1)).findAll();
    }
    
    @Test
    void getQuoteStats_WithEmptyList_ShouldReturnZeroStats() {
        // Arrange
        when(quoteRepository.findAll())
                .thenReturn(Flux.empty());
        
        // Act & Assert
        StepVerifier.create(quoteService.getQuoteStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(0L);
                    assertThat(stats.get("pendientes")).isEqualTo(0L);
                    assertThat(stats.get("prioridadAlta")).isEqualTo(0L);
                })
                .verifyComplete();
    }
}
