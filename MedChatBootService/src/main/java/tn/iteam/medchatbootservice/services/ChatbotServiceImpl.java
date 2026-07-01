package tn.iteam.medchatbootservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;
import tn.iteam.medchatbootservice.exceptions.ChatbotUnavailableException;

@Service
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {
    private final ChatClient chatClient;

    public ChatbotServiceImpl(ChatClient.Builder chatClientBuilder,
                              @Value("${medback.chatbot.system-prompt}") String systemPrompt) {
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    @Override
    public ChatResponse reply(ChatRequest request) {
        try {
            String prompt = buildUserPrompt(request);
            String reply = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (!StringUtils.hasText(reply)) {
                throw new ChatbotUnavailableException("The local Ollama model returned an empty response.");
            }

            return ChatResponse.builder()
                    .reply(reply)
                    .build();
        } catch (ChatbotUnavailableException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("Chatbot request failed for doctorId={}", request.getDoctorId(), exception);
            throw new ChatbotUnavailableException(
                    "Chatbot request failed. Verify that Ollama is running and the llama3 model is available.",
                    exception
            );
        }
    }


    private String buildUserPrompt(ChatRequest request) {
        StringBuilder promptBuilder = new StringBuilder()
                .append("Reported symptoms: ")
                .append(request.getMessage().trim());

        if (StringUtils.hasText(request.getDoctorId())) {
            promptBuilder.append(System.lineSeparator())
                    .append("Doctor context id: ")
                    .append(request.getDoctorId().trim());
        }

        promptBuilder.append(System.lineSeparator())
                .append("Return concise clinical insights, likely considerations, and suggested next steps.");

        return promptBuilder.toString();
    }
}
