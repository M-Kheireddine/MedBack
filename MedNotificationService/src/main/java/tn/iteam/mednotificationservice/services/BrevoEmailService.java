package tn.iteam.mednotificationservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Slf4j
@Service
public class BrevoEmailService {
    private final String brevoApiKey;
    private final String senderEmail;
    private final String senderName;
    private final boolean simulationEnabled;
    private volatile TransactionalEmailsApi transactionalEmailsApi;

    public BrevoEmailService(
            @Value("${brevo.api.key:}") String brevoApiKey,
            @Value("${brevo.sender.email}") String senderEmail,
            @Value("${brevo.sender.name:MedBack Notifications}") String senderName,
            @Value("${brevo.simulation-enabled:false}") boolean simulationEnabled
    ) {
        this.brevoApiKey = brevoApiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.simulationEnabled = simulationEnabled;
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email must not be blank.");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Email subject must not be blank.");
        }
        if (htmlContent == null || htmlContent.isBlank()) {
            throw new IllegalArgumentException("Email HTML content must not be blank.");
        }

        if (simulationEnabled) {
            log.info("Simulated Brevo email sent to={} subject={}", to, subject);
            return;
        }

        validateConfiguration();
        TransactionalEmailsApi emailsApi = getTransactionalEmailsApi();

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        sendSmtpEmail.setSender(new SendSmtpEmailSender().email(senderEmail).name(senderName));
        sendSmtpEmail.setTo(List.of(new SendSmtpEmailTo().email(to)));
        sendSmtpEmail.setSubject(subject);
        sendSmtpEmail.setHtmlContent(htmlContent);

        try {
            CreateSmtpEmail response = emailsApi.sendTransacEmail(sendSmtpEmail);
            log.info("Brevo transactional email accepted. recipient={} messageId={}", to, response.getMessageId());
        } catch (ApiException exception) {
            log.error("Brevo email delivery failed for recipient={} statusCode={} responseBody={}",
                    to,
                    exception.getCode(),
                    exception.getResponseBody());
            throw new IllegalStateException("Unable to send transactional email through Brevo.", exception);
        } catch (LinkageError linkageError) {
            log.error("Brevo client dependency mismatch detected while sending email to={}. message={}",
                    to,
                    linkageError.getMessage(),
                    linkageError);
            throw new IllegalStateException("Brevo client dependencies are misaligned at runtime.", linkageError);
        }
    }

    private TransactionalEmailsApi getTransactionalEmailsApi() {
        TransactionalEmailsApi currentApi = transactionalEmailsApi;
        if (currentApi != null) {
            return currentApi;
        }

        synchronized (this) {
            if (transactionalEmailsApi == null) {
                ApiClient currentClient = createApiClient();
                transactionalEmailsApi = new TransactionalEmailsApi(currentClient);
            }
            return transactionalEmailsApi;
        }
    }

    private ApiClient createApiClient() {
        ApiClient currentClient = new ApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) currentClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(brevoApiKey);
        return currentClient;
    }

    private void validateConfiguration() {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            throw new IllegalStateException("Brevo API key is not configured. Set BREVO_API_KEY.");
        }

        if (senderEmail == null || senderEmail.isBlank()) {
            throw new IllegalStateException("Brevo sender email is not configured. Set BREVO_SENDER_EMAIL.");
        }
    }
}
