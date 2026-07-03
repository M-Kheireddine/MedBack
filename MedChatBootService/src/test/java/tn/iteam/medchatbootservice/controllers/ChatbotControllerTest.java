package tn.iteam.medchatbootservice.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;
import tn.iteam.medchatbootservice.services.ChatbotService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotControllerTest {

    @Mock
    private ChatbotService chatbotService;

    @InjectMocks
    private ChatbotController chatbotController;

    @Test
    void chatShouldDelegateToChatbotService() {
        ChatRequest request = ChatRequest.builder()
                .message("fever and fatigue")
                .doctorId("doctor-1")
                .build();
        ChatResponse expectedResponse = ChatResponse.builder()
                .reply("Consider influenza and monitor hydration.")
                .build();

        when(chatbotService.reply(request)).thenReturn(expectedResponse);

        ResponseEntity<ChatResponse> response = chatbotController.chat(request);

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedResponse, response.getBody());
        verify(chatbotService).reply(request);
    }
}
