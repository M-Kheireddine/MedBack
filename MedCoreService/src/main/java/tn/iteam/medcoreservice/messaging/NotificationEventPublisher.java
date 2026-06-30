package tn.iteam.medcoreservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.dtos.events.NotificationEvent;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.Prescription;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventPublisher {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Value("${medback.kafka.topics.appointment:appointment_topic}")
    private String appointmentTopic;

    @Value("${medback.kafka.topics.prescription:prescription_topic}")
    private String prescriptionTopic;

    public void publishAppointmentCreated(Appointment appointment, String recipientEmail) {
        NotificationEvent event = NotificationEvent.builder()
                .type("APPOINTMENT_CREATED")
                .recipientEmail(recipientEmail)
                .subject("Appointment scheduled")
                .message("A new appointment has been scheduled from " + appointment.getStartDateTime()
                        + " to " + appointment.getEndDateTime() + ".")
                .build();

        kafkaTemplate.send(appointmentTopic, appointment.getId(), event);
        log.info("Appointment event published for appointmentId={}", appointment.getId());
    }

    public void publishPrescriptionCreated(Prescription prescription, String recipientEmail) {
        NotificationEvent event = NotificationEvent.builder()
                .type("PRESCRIPTION_CREATED")
                .recipientEmail(recipientEmail)
                .subject("Prescription created")
                .message("A new prescription has been created by doctor " + prescription.getDoctorId() + ".")
                .build();

        kafkaTemplate.send(prescriptionTopic, prescription.getId(), event);
        log.info("Prescription event published for prescriptionId={}", prescription.getId());
    }
}
