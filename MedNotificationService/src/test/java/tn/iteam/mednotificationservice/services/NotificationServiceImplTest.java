package tn.iteam.mednotificationservice.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.mednotificationservice.dtos.events.NotificationEvent;
import tn.iteam.mednotificationservice.dtos.responses.NotificationStatusResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private BrevoEmailService brevoEmailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendNotificationShouldSkipNullEvent() {
        notificationService.sendNotification(null);

        verifyNoInteractions(brevoEmailService);
    }

    @Test
    void sendNotificationShouldSkipEventWithoutRecipient() {
        NotificationEvent event = NotificationEvent.builder()
                .type("APPOINTMENT_CREATED")
                .message("Appointment confirmed")
                .build();

        notificationService.sendNotification(event);

        verifyNoInteractions(brevoEmailService);
    }

    @Test
    void sendNotificationShouldResolveAppointmentSubjectAndEscapeHtmlContent() {
        NotificationEvent event = NotificationEvent.builder()
                .type("APPOINTMENT_CREATED")
                .recipientEmail("patient@medback.com")
                .message("<check>\nRemember fasting")
                .build();

        ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);

        notificationService.sendNotification(event);

        verify(brevoEmailService).sendHtmlEmail(
                eq("patient@medback.com"),
                eq("MedBack Appointment Notification"),
                htmlCaptor.capture()
        );
        assertTrue(htmlCaptor.getValue().contains("&lt;check&gt;"));
        assertTrue(htmlCaptor.getValue().contains("Remember fasting"));
        assertTrue(htmlCaptor.getValue().contains("<br/>"));
    }

    @Test
    void sendNotificationShouldPreserveCustomSubject() {
        NotificationEvent event = NotificationEvent.builder()
                .type("CUSTOM")
                .recipientEmail("patient@medback.com")
                .subject("Custom subject")
                .message("Message")
                .build();

        notificationService.sendNotification(event);

        verify(brevoEmailService).sendHtmlEmail(eq("patient@medback.com"), eq("Custom subject"), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void sendNotificationShouldResolvePrescriptionSubject() {
        NotificationEvent event = NotificationEvent.builder()
                .type("PRESCRIPTION_CREATED")
                .recipientEmail("patient@medback.com")
                .message("New prescription")
                .build();

        notificationService.sendNotification(event);

        verify(brevoEmailService).sendHtmlEmail(eq("patient@medback.com"), eq("MedBack Prescription Notification"), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void sendNotificationShouldFallbackToGenericSubjectForUnknownType() {
        NotificationEvent event = NotificationEvent.builder()
                .type("UNHANDLED_EVENT")
                .recipientEmail("patient@medback.com")
                .message("\"quoted\" and 'apostrophe'")
                .build();

        ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);

        notificationService.sendNotification(event);

        verify(brevoEmailService).sendHtmlEmail(eq("patient@medback.com"), eq("MedBack Notification"), htmlCaptor.capture());
        assertTrue(htmlCaptor.getValue().contains("&quot;quoted&quot;"));
        assertTrue(htmlCaptor.getValue().contains("&#39;apostrophe&#39;"));
    }

    @Test
    void getStatusShouldReturnReadyStatus() {
        NotificationStatusResponseDto response = notificationService.getStatus();

        assertEquals("med-notification-service", response.getService());
        assertEquals("READY", response.getStatus());
        verify(brevoEmailService, never()).sendHtmlEmail(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }
}
