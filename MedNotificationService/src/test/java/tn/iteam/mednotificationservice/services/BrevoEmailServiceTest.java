package tn.iteam.mednotificationservice.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrevoEmailServiceTest {

    @Test
    void sendHtmlEmailShouldReturnImmediatelyWhenSimulationIsEnabled() {
        BrevoEmailService brevoEmailService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        assertDoesNotThrow(() -> brevoEmailService.sendHtmlEmail("patient@medback.com", "Subject", "<p>Hello</p>"));
    }

    @Test
    void sendHtmlEmailShouldRejectBlankRecipient() {
        BrevoEmailService brevoEmailService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brevoEmailService.sendHtmlEmail(" ", "Subject", "<p>Hello</p>")
        );

        assertEquals("Recipient email must not be blank.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldRejectBlankSubject() {
        BrevoEmailService brevoEmailService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brevoEmailService.sendHtmlEmail("patient@medback.com", " ", "<p>Hello</p>")
        );

        assertEquals("Email subject must not be blank.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldRejectBlankHtmlContent() {
        BrevoEmailService brevoEmailService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> brevoEmailService.sendHtmlEmail("patient@medback.com", "Subject", " ")
        );

        assertEquals("Email HTML content must not be blank.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldRequireApiKeyWhenSimulationIsDisabled() {
        BrevoEmailService brevoEmailService = new BrevoEmailService("", "sender@medback.com", "MedBack", false);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> brevoEmailService.sendHtmlEmail("patient@medback.com", "Subject", "<p>Hello</p>")
        );

        assertEquals("Brevo API key is not configured. Set BREVO_API_KEY.", exception.getMessage());
    }
}
