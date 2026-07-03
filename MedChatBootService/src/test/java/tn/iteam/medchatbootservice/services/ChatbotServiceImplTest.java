package tn.iteam.medchatbootservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;
import tn.iteam.medchatbootservice.exceptions.ChatbotUnavailableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceImplTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    private ChatbotServiceImpl chatbotService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.defaultSystem("Medical prompt")).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        chatbotService = new ChatbotServiceImpl(chatClientBuilder, "Medical prompt");
    }

    @Test
    void constructorShouldConfigureDefaultSystemPrompt() {
        verify(chatClientBuilder).defaultSystem("Medical prompt");
        verify(chatClientBuilder).build();
    }

    @Test
    void replyShouldReturnChatbotResponseWhenOllamaReturnsContent() {
        when(chatClient.prompt().user(anyString()).call().content()).thenReturn("Likely viral infection.");

        ChatResponse response = chatbotService.reply(ChatRequest.builder()
                .message("fever and sore throat")
                .doctorId("doctor-1")
                .build());

        assertEquals("Likely viral infection.", response.getReply());
    }

    @Test
    void replyShouldThrowWhenOllamaReturnsEmptyContent() {
        when(chatClient.prompt().user(anyString()).call().content()).thenReturn(" ");

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
    void replyShouldWrapUnexpectedExceptions() {
        when(chatClient.prompt().user(anyString()).call().content()).thenThrow(new RuntimeException("Ollama unavailable"));

        ChatbotUnavailableException exception = assertThrows(
                ChatbotUnavailableException.class,
                () -> chatbotService.reply(ChatRequest.builder()
                        .message("shortness of breath")
                        .doctorId("doctor-3")
                        .build())
        );

        assertEquals("Chatbot request failed. Verify that Ollama is running and the llama3 model is available.", exception.getMessage());
    }
}
