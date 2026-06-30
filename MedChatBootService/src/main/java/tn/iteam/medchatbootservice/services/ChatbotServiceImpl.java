package tn.iteam.medchatbootservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;
import tn.iteam.medchatbootservice.exceptions.ChatbotUnavailableException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {
    private final ChatClient chatClient;

    @Value("${spring.ai.openai.api-key:}")
    private String openAiApiKey;

    @Override
    public ChatResponse reply(ChatRequest request) {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            throw new ChatbotUnavailableException(
                    "OpenAI API key is not configured. Set OPENAI_API_KEY or SPRING_AI_OPENAI_API_KEY before using the chatbot."
            );
        }

        try {
            String prompt = buildPrompt(request);
            String reply = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            return ChatResponse.builder()
                    .reply(reply)
                    .build();
        } catch (Exception exception) {
            log.error("Chatbot request failed for doctorId={}", request.getDoctorId(), exception);
            throw new ChatbotUnavailableException(
                    "Chatbot request failed. Verify the OpenAI API key and model configuration.",
                    exception
            );
        }
    }


    private String buildPrompt(ChatRequest request) {
        if (request.getDoctorId() == null || request.getDoctorId().isBlank()) {
            return request.getMessage();
        }

        return "Doctor context id: " + request.getDoctorId() + System.lineSeparator()
                + "User message: " + request.getMessage();
    }
}
