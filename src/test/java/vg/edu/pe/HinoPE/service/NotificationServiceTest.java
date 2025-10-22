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
import vg.edu.pe.HinoPE.model.entity.Notification;
import vg.edu.pe.HinoPE.repository.NotificationRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setTipo("alert");
        testNotification.setPrioridad("alta");
        testNotification.setTitulo("Nueva cotización");
        testNotification.setMensaje("Se ha recibido una nueva cotización");
        testNotification.setVehiculoId(1L);
        testNotification.setQuoteId(1L);
        testNotification.setUserId(1L);
        testNotification.setLeido(false);
        testNotification.setCreatedAt(LocalDateTime.now());
        testNotification.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotifications() {
        // Arrange
        when(notificationRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Flux.just(testNotification));

        // Act & Assert
        StepVerifier.create(notificationService.getAllNotifications())
                .expectNext(testNotification)
                .verifyComplete();

        verify(notificationRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getNotificationsByReadStatus_ShouldReturnFilteredNotifications() {
        // Arrange
        when(notificationRepository.findByLeido(false))
                .thenReturn(Flux.just(testNotification));

        // Act & Assert
        StepVerifier.create(notificationService.getNotificationsByReadStatus(false))
                .expectNext(testNotification)
                .verifyComplete();

        verify(notificationRepository, times(1)).findByLeido(false);
    }

    @Test
    void getNotificationsByType_ShouldReturnFilteredNotifications() {
        // Arrange
        when(notificationRepository.findByTipo("alert"))
                .thenReturn(Flux.just(testNotification));

        // Act & Assert
        StepVerifier.create(notificationService.getNotificationsByType("alert"))
                .expectNext(testNotification)
                .verifyComplete();

        verify(notificationRepository, times(1)).findByTipo("alert");
    }

    @Test
    void getNotificationById_WhenExists_ShouldReturnNotification() {
        // Arrange
        when(notificationRepository.findById(1L))
                .thenReturn(Mono.just(testNotification));

        // Act & Assert
        StepVerifier.create(notificationService.getNotificationById(1L))
                .expectNext(testNotification)
                .verifyComplete();

        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getNotificationById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(notificationRepository.findById(999L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(notificationService.getNotificationById(999L))
                .verifyComplete();

        verify(notificationRepository, times(1)).findById(999L);
    }

    @Test
    void createNotification_ShouldSetDefaultsAndSave() {
        // Arrange
        Notification newNotification = new Notification();
        newNotification.setTitulo("Test Notification");
        newNotification.setMensaje("Test message");

        Notification savedNotification = new Notification();
        savedNotification.setId(2L);
        savedNotification.setTitulo("Test Notification");
        savedNotification.setMensaje("Test message");
        savedNotification.setLeido(false);
        savedNotification.setCreatedAt(LocalDateTime.now());
        savedNotification.setUpdatedAt(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(savedNotification));

        // Act & Assert
        StepVerifier.create(notificationService.createNotification(newNotification))
                .assertNext(notification -> {
                    assertThat(notification.getId()).isEqualTo(2L);
                    assertThat(notification.getLeido()).isFalse();
                    assertThat(notification.getCreatedAt()).isNotNull();
                })
                .verifyComplete();

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createNotification_WithLeidoTrue_ShouldKeepValue() {
        // Arrange
        Notification newNotification = new Notification();
        newNotification.setTitulo("Test");
        newNotification.setLeido(true);

        Notification savedNotification = new Notification();
        savedNotification.setId(3L);
        savedNotification.setLeido(true);

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(savedNotification));

        // Act & Assert
        StepVerifier.create(notificationService.createNotification(newNotification))
                .assertNext(notification -> {
                    assertThat(notification.getLeido()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void markAsRead_WhenExists_ShouldUpdateAndReturn() {
        // Arrange
        Notification updatedNotification = new Notification();
        updatedNotification.setId(1L);
        updatedNotification.setLeido(true);

        when(notificationRepository.findById(1L))
                .thenReturn(Mono.just(testNotification));
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(updatedNotification));

        // Act & Assert
        StepVerifier.create(notificationService.markAsRead(1L))
                .assertNext(notification -> {
                    assertThat(notification.getLeido()).isTrue();
                })
                .verifyComplete();

        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(notificationRepository.findById(999L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(notificationService.markAsRead(999L))
                .verifyComplete();

        verify(notificationRepository, times(1)).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void markAllAsRead_ShouldUpdateAllUnreadNotifications() {
        // Arrange
        Notification unread1 = new Notification();
        unread1.setId(1L);
        unread1.setLeido(false);

        Notification unread2 = new Notification();
        unread2.setId(2L);
        unread2.setLeido(false);

        Notification saved1 = new Notification();
        saved1.setId(1L);
        saved1.setLeido(true);

        Notification saved2 = new Notification();
        saved2.setId(2L);
        saved2.setLeido(true);

        when(notificationRepository.findByLeido(false))
                .thenReturn(Flux.just(unread1, unread2));
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(saved1))
                .thenReturn(Mono.just(saved2));

        // Act & Assert
        StepVerifier.create(notificationService.markAllAsRead())
                .verifyComplete();

        verify(notificationRepository, times(1)).findByLeido(false);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void markAllAsRead_WithNoUnreadNotifications_ShouldComplete() {
        // Arrange
        when(notificationRepository.findByLeido(false))
                .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(notificationService.markAllAsRead())
                .verifyComplete();

        verify(notificationRepository, times(1)).findByLeido(false);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteNotification_ShouldCallRepository() {
        // Arrange
        when(notificationRepository.deleteById(1L))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(notificationService.deleteNotification(1L))
                .verifyComplete();

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void getNotificationStats_ShouldReturnCorrectStatistics() {
        // Arrange
        Notification notif1 = new Notification();
        notif1.setLeido(false);
        notif1.setPrioridad("alta");

        Notification notif2 = new Notification();
        notif2.setLeido(true);
        notif2.setPrioridad("media");

        Notification notif3 = new Notification();
        notif3.setLeido(false);
        notif3.setPrioridad("baja");

        Notification notif4 = new Notification();
        notif4.setLeido(true);
        notif4.setPrioridad("alta");

        when(notificationRepository.findAll())
                .thenReturn(Flux.just(notif1, notif2, notif3, notif4));

        // Act & Assert
        StepVerifier.create(notificationService.getNotificationStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(4L);
                    assertThat(stats.get("noLeidas")).isEqualTo(2L);
                    assertThat(stats.get("leidas")).isEqualTo(2L);
                    assertThat(stats.get("prioridadAlta")).isEqualTo(2L);
                    assertThat(stats.get("prioridadMedia")).isEqualTo(1L);
                    assertThat(stats.get("prioridadBaja")).isEqualTo(1L);
                })
                .verifyComplete();

        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void getNotificationStats_WithEmptyList_ShouldReturnZeroStats() {
        // Arrange
        when(notificationRepository.findAll())
                .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(notificationService.getNotificationStats())
                .assertNext(stats -> {
                    assertThat(stats.get("total")).isEqualTo(0L);
                    assertThat(stats.get("noLeidas")).isEqualTo(0L);
                    assertThat(stats.get("leidas")).isEqualTo(0L);
                })
                .verifyComplete();
    }
}
