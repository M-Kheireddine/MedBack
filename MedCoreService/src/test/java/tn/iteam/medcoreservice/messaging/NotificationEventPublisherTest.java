package tn.iteam.medcoreservice.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import tn.iteam.medcoreservice.dtos.events.NotificationEvent;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.Prescription;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventPublisherTest {

    @Mock
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    private NotificationEventPublisher notificationEventPublisher;

    @BeforeEach
    void setUp() {
        notificationEventPublisher = new NotificationEventPublisher(kafkaTemplate);
        ReflectionTestUtils.setField(notificationEventPublisher, "appointmentTopic", "appointment_topic");
        ReflectionTestUtils.setField(notificationEventPublisher, "prescriptionTopic", "prescription_topic");
    }

    @Test
    void publishAppointmentCreatedShouldSendExpectedEvent() {
        Appointment appointment = Appointment.builder()
                .id("appointment-1")
                .startDateTime(LocalDateTime.of(2026, 7, 3, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 7, 3, 10, 30))
                .build();

        notificationEventPublisher.publishAppointmentCreated(appointment, "patient@example.com");

        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("appointment_topic"), org.mockito.ArgumentMatchers.eq("appointment-1"), eventCaptor.capture());

        NotificationEvent event = eventCaptor.getValue();
        assertEquals("APPOINTMENT_CREATED", event.getType());
        assertEquals("patient@example.com", event.getRecipientEmail());
        assertEquals("Appointment scheduled", event.getSubject());
        assertTrue(event.getMessage().contains("2026-07-03T10:00"));
    }

    @Test
    void publishPrescriptionCreatedShouldSendExpectedEventWhenPrescriptionLinesAreNull() {
        Prescription prescription = Prescription.builder()
                .id("prescription-1")
                .doctorId("doctor-1")
                .patientId("patient-1")
                .prescriptionLines(null)
                .build();

        notificationEventPublisher.publishPrescriptionCreated(prescription, "patient@example.com");

        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("prescription_topic"), org.mockito.ArgumentMatchers.eq("prescription-1"), eventCaptor.capture());

        NotificationEvent event = eventCaptor.getValue();
        assertEquals("PRESCRIPTION_CREATED", event.getType());
        assertEquals("Prescription created", event.getSubject());
        assertTrue(event.getMessage().contains("0 medication line(s)"));
        assertTrue(event.getMessage().contains("doctor doctor-1"));
    }
}
