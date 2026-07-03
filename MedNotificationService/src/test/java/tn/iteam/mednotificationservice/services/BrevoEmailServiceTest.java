package tn.iteam.mednotificationservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrevoEmailServiceTest {

    @Mock
    private TransactionalEmailsApi transactionalEmailsApi;

    private BrevoEmailService brevoEmailService;

    @BeforeEach
    void setUp() {
        brevoEmailService = new BrevoEmailService("brevo-key", "sender@medback.com", "MedBack", false);
    }

    @Test
    void sendHtmlEmailShouldReturnImmediatelyWhenSimulationIsEnabled() {
        BrevoEmailService simulatedService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        assertDoesNotThrow(() -> simulatedService.sendHtmlEmail("patient@medback.com", "Subject", "<p>Hello</p>"));
    }

    @Test
    void sendHtmlEmailShouldRejectBlankRecipient() {
        BrevoEmailService simulatedService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> simulatedService.sendHtmlEmail(" ", "Subject", "<p>Hello</p>")
        );

        assertEquals("Recipient email must not be blank.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldRejectBlankSubject() {
        BrevoEmailService simulatedService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> simulatedService.sendHtmlEmail("patient@medback.com", " ", "<p>Hello</p>")
        );

        assertEquals("Email subject must not be blank.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldRejectBlankHtmlContent() {
        BrevoEmailService simulatedService = new BrevoEmailService("", "sender@medback.com", "MedBack", true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> simulatedService.sendHtmlEmail("patient@medback.com", "Subject", " ")
        );

        assertEquals("Email HTML content must not be blank.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldRequireApiKeyWhenSimulationIsDisabled() {
        BrevoEmailService missingApiKeyService = new BrevoEmailService("", "sender@medback.com", "MedBack", false);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> missingApiKeyService.sendHtmlEmail("patient@medback.com", "Subject", "<p>Hello</p>")
        );

        assertEquals("Brevo API key is not configured. Set BREVO_API_KEY.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldRequireSenderEmailWhenSimulationIsDisabled() {
        BrevoEmailService missingSenderService = new BrevoEmailService("brevo-key", " ", "MedBack", false);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> missingSenderService.sendHtmlEmail("patient@medback.com", "Subject", "<p>Hello</p>")
        );

        assertEquals("Brevo sender email is not configured. Set BREVO_SENDER_EMAIL.", exception.getMessage());
    }

    @Test
    void sendHtmlEmailShouldDelegateToBrevoTransactionalApiWhenConfigured() throws Exception {
        ReflectionTestUtils.setField(brevoEmailService, "transactionalEmailsApi", transactionalEmailsApi);
        when(transactionalEmailsApi.sendTransacEmail(any(SendSmtpEmail.class))).thenReturn(new CreateSmtpEmail());

        brevoEmailService.sendHtmlEmail("patient@medback.com", "Prescription", "<p>Medication details</p>");

        ArgumentCaptor<SendSmtpEmail> emailCaptor = ArgumentCaptor.forClass(SendSmtpEmail.class);
        verify(transactionalEmailsApi).sendTransacEmail(emailCaptor.capture());

        SendSmtpEmail payload = emailCaptor.getValue();
        assertEquals("sender@medback.com", payload.getSender().getEmail());
        assertEquals("MedBack", payload.getSender().getName());
        assertEquals("patient@medback.com", payload.getTo().getFirst().getEmail());
        assertEquals("Prescription", payload.getSubject());
        assertEquals("<p>Medication details</p>", payload.getHtmlContent());
    }

    @Test
    void sendHtmlEmailShouldWrapApiException() throws Exception {
        ReflectionTestUtils.setField(brevoEmailService, "transactionalEmailsApi", transactionalEmailsApi);
        when(transactionalEmailsApi.sendTransacEmail(any(SendSmtpEmail.class)))
                .thenThrow(new ApiException(502, "Brevo error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> brevoEmailService.sendHtmlEmail("patient@medback.com", "Prescription", "<p>Medication details</p>")
        );

        assertEquals("Unable to send transactional email through Brevo.", exception.getMessage());
    }

    @Test
    void getTransactionalEmailsApiShouldInitializeOnceAndReuseInstance() {
        TransactionalEmailsApi firstApi = ReflectionTestUtils.invokeMethod(brevoEmailService, "getTransactionalEmailsApi");
        TransactionalEmailsApi secondApi = ReflectionTestUtils.invokeMethod(brevoEmailService, "getTransactionalEmailsApi");

        assertNotNull(firstApi);
        assertSame(firstApi, secondApi);
    }
}
