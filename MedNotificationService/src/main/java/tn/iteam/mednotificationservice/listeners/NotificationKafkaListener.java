package tn.iteam.mednotificationservice.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tn.iteam.mednotificationservice.dtos.events.NotificationEvent;
import tn.iteam.mednotificationservice.services.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${medback.kafka.topics.appointment:appointment_topic}",
            groupId = "${spring.kafka.consumer.group-id:med-notification-group}",
            containerFactory = "notificationEventKafkaListenerContainerFactory"
    )
    public void handleAppointmentNotification(NotificationEvent event) {
        log.info("Appointment notification event received for recipient={}", event.getRecipientEmail());
        notificationService.sendNotification(event);
    }

    @KafkaListener(
            topics = "${medback.kafka.topics.prescription:prescription_topic}",
            groupId = "${spring.kafka.consumer.group-id:med-notification-group}",
            containerFactory = "notificationEventKafkaListenerContainerFactory"
    )
    public void handlePrescriptionNotification(NotificationEvent event) {
        log.info("Prescription notification event received for recipient={}", event.getRecipientEmail());
        notificationService.sendNotification(event);
    }
}
