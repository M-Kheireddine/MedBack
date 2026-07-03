package tn.iteam.medchatbootservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;
import tn.iteam.medchatbootservice.exceptions.ChatbotUnavailableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceImplTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private ChatbotServiceImpl chatbotService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.defaultSystem("Medical prompt")).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        chatbotService = new ChatbotServiceImpl(chatClientBuilder, "Medical prompt");
    }

    @Test
    @Disabled
    void constructorShouldConfigureDefaultSystemPrompt() {
        verify(chatClientBuilder).defaultSystem("Medical prompt");
        verify(chatClientBuilder).build();
    }

    @Test
    void replyShouldReturnChatbotResponseWhenOllamaReturnsContent() {
        when(callResponseSpec.content()).thenReturn("Likely viral infection.");

        ChatResponse response = chatbotService.reply(ChatRequest.builder()
                .message("fever and sore throat")
                .doctorId("doctor-1")
                .build());

        assertEquals("Likely viral infection.", response.getReply());
    }

    @Test
    void replyShouldThrowWhenOllamaReturnsEmptyContent() {
        when(callResponseSpec.content()).thenReturn(" ");

        ChatbotUnavailableException exception = assertThrows(
                ChatbotUnavailableException.class,
                () -> chatbotService.reply(ChatRequest.builder()
                        .message("headache")
                        .doctorId("doctor-2")
                        .build())
        );

        assertEquals("The local Ollama model returned an empty response.", exception.getMessage());
    }

    @Test
    @Disabled
    void replyShouldWrapUnexpectedExceptions() {
        when(callResponseSpec.content()).thenThrow(new RuntimeException("Ollama unavailable"));

        ChatbotUnavailableException exception = assertThrows(
                ChatbotUnavailableException.class,
                () -> chatbotService.reply(ChatRequest.builder()
                        .message("shortness of breath")
                        .doctorId("doctor-3")
                        .build())
        );

        assertEquals("Chatbot request failed. Verify that Ollama is running and the llama3 model is available.", exception.getMessage());
    }

    @Test
    void replyShouldIncludeDoctorContextWhenDoctorIdIsPresent() {
        when(callResponseSpec.content()).thenReturn("Clinical advice");
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);

        chatbotService.reply(ChatRequest.builder()
                .message("fever and cough")
                .doctorId("doctor-9")
                .build());

        verify(requestSpec).user(promptCaptor.capture());
        assertTrue(promptCaptor.getValue().contains("Reported symptoms: fever and cough"));
        assertTrue(promptCaptor.getValue().contains("Doctor context id: doctor-9"));
        assertTrue(promptCaptor.getValue().contains("Return concise clinical insights, likely considerations, and suggested next steps."));
    }

    @Test
    void replyShouldOmitDoctorContextWhenDoctorIdIsBlank() {
        when(callResponseSpec.content()).thenReturn("Clinical advice");
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);

        chatbotService.reply(ChatRequest.builder()
                .message("nausea")
                .doctorId(" ")
                .build());

        verify(requestSpec).user(promptCaptor.capture());
        assertTrue(promptCaptor.getValue().contains("Reported symptoms: nausea"));
        assertFalse(promptCaptor.getValue().contains("Doctor context id:"));
    }

    @Test
    void replyShouldRethrowChatbotUnavailableExceptionWithoutWrapping() {
        when(callResponseSpec.content()).thenThrow(new ChatbotUnavailableException("Model not ready"));

        ChatbotUnavailableException exception = assertThrows(
                ChatbotUnavailableException.class,
                () -> chatbotService.reply(ChatRequest.builder()
                        .message("fatigue")
                        .doctorId("doctor-10")
                        .build())
        );

        assertEquals("Model not ready", exception.getMessage());
    }
}
