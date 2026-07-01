package tn.iteam.medchatbootservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;
import tn.iteam.medchatbootservice.services.ChatbotService;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/chat", "/v1/chat"})
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatbotService.reply(request));
    }
}
