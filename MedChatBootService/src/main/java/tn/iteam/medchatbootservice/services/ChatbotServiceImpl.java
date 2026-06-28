package tn.iteam.medchatbootservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;
import tn.iteam.medchatbootservice.exceptions.ChatbotUnavailableException;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {
    private final ChatClient chatClient;

    @Override
    public ChatResponse reply(ChatRequest request) {
        try {
            String prompt = buildPrompt(request);
            String reply = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            return ChatResponse.builder()
                    .reply(reply)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String buildPrompt(ChatRequest request) {
        if (request.getDoctorId() == null || request.getDoctorId().isBlank()) {
            return request.getMessage();
        }

        return "Doctor context id: " + request.getDoctorId() + System.lineSeparator()
                + "User message: " + request.getMessage();
    }
}
