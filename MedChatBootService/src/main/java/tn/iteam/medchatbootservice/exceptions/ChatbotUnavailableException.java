package tn.iteam.medchatbootservice.exceptions;

public class ChatbotUnavailableException extends RuntimeException {
    public ChatbotUnavailableException(String message) {
        super(message);
    }

    public ChatbotUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
