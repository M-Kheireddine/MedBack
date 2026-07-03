package tn.iteam.mednotificationservice.listeners;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.mednotificationservice.dtos.events.NotificationEvent;
import tn.iteam.mednotificationservice.services.NotificationService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationKafkaListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationKafkaListener notificationKafkaListener;

    @Test
    void handleAppointmentNotificationShouldDelegateToNotificationService() {
        NotificationEvent event = NotificationEvent.builder()
                .type("APPOINTMENT_CREATED")
                .recipientEmail("patient@medback.com")
                .message("Appointment created")
                .build();

        notificationKafkaListener.handleAppointmentNotification(event);

        verify(notificationService).sendNotification(event);
    }

    @Test
    void handlePrescriptionNotificationShouldDelegateToNotificationService() {
        NotificationEvent event = NotificationEvent.builder()
                .type("PRESCRIPTION_CREATED")
                .recipientEmail("patient@medback.com")
                .message("Prescription created")
                .build();

        notificationKafkaListener.handlePrescriptionNotification(event);

        verify(notificationService).sendNotification(event);
    }
}
