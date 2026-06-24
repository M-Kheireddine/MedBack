package tn.iteam.mednotificationservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.iteam.mednotificationservice.dtos.events.NotificationEvent;
import tn.iteam.mednotificationservice.dtos.responses.NotificationStatusResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender javaMailSender;

    @Value("${medback.mail.from:no-reply@medback.local}")
    private String senderEmail;

    @Value("${medback.mail.simulation-enabled:true}")
    private boolean simulationEnabled;

    @Override
    public void sendNotification(NotificationEvent event) {
        if (event.getRecipientEmail() == null || event.getRecipientEmail().isBlank()) {
            log.warn("Notification skipped because recipient email is missing. type={}", event.getType());
            return;
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom(senderEmail);
            helper.setTo(event.getRecipientEmail());
            helper.setSubject(event.getSubject());
            helper.setText(event.getMessage(), false);

            if (simulationEnabled) {
                log.info("Simulated email sent to={} subject={} message={}",
                        event.getRecipientEmail(), event.getSubject(), event.getMessage());
                return;
            }

            javaMailSender.send(mimeMessage);
            log.info("Email sent to={} subject={}", event.getRecipientEmail(), event.getSubject());
        } catch (MessagingException exception) {
            throw new IllegalStateException("Unable to prepare notification email", exception);
        }
    }

    @Override
    public NotificationStatusResponseDto getStatus() {
        return NotificationStatusResponseDto.builder()
                .service("med-notification-service")
                .status("READY")
                .build();
    }
}
