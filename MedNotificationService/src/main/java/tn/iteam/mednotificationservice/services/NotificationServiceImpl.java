package tn.iteam.mednotificationservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.iteam.mednotificationservice.dtos.events.NotificationEvent;
import tn.iteam.mednotificationservice.dtos.responses.NotificationStatusResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final BrevoEmailService brevoEmailService;

    @Override
    public void sendNotification(NotificationEvent event) {
        if (event == null) {
            log.warn("Notification skipped because event payload is null.");
            return;
        }

        if (event.getRecipientEmail() == null || event.getRecipientEmail().isBlank()) {
            log.warn("Notification skipped because recipient email is missing. type={}", event.getType());
            return;
        }

        String subject = resolveSubject(event);
        String htmlContent = buildHtmlContent(event);

        brevoEmailService.sendHtmlEmail(event.getRecipientEmail(), subject, htmlContent);
        log.info("Notification email delegated to Brevo. recipient={} type={}", event.getRecipientEmail(), event.getType());
    }

    @Override
    public NotificationStatusResponseDto getStatus() {
        return NotificationStatusResponseDto.builder()
                .service("med-notification-service")
                .status("READY")
                .build();
    }

    private String resolveSubject(NotificationEvent event) {
        if (event.getSubject() != null && !event.getSubject().isBlank()) {
            return event.getSubject();
        }

        return switch (event.getType() == null ? "" : event.getType().trim().toUpperCase()) {
            case "PRESCRIPTION_CREATED", "PRESCRIPTION_UPDATED" -> "MedBack Prescription Notification";
            case "APPOINTMENT_CREATED", "APPOINTMENT_UPDATED", "APPOINTMENT_STATUS_UPDATED" ->
                    "MedBack Appointment Notification";
            default -> "MedBack Notification";
        };
    }

    private String buildHtmlContent(NotificationEvent event) {
        String safeType = escapeHtml(event.getType() == null ? "Notification" : event.getType());
        String safeMessage = escapeHtml(event.getMessage() == null ? "" : event.getMessage())
                .replace(System.lineSeparator(), "<br/>")
                .replace("\n", "<br/>");

        return """
                <html>
                  <body style="margin:0;padding:24px;background:#f5f7fb;font-family:Arial,sans-serif;color:#1f2937;">
                    <div style="max-width:640px;margin:0 auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:16px;overflow:hidden;">
                      <div style="padding:20px 24px;background:#dbeafe;border-bottom:1px solid #bfdbfe;">
                        <h2 style="margin:0;font-size:20px;color:#1d4ed8;">MedBack Notification</h2>
                        <p style="margin:8px 0 0 0;font-size:13px;color:#475569;">%s</p>
                      </div>
                      <div style="padding:24px;">
                        <p style="margin:0 0 16px 0;font-size:14px;line-height:1.7;">%s</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(safeType, safeMessage);
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
