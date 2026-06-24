package tn.iteam.medchatbootservice.services;

import tn.iteam.medchatbootservice.dtos.requests.ChatRequest;
import tn.iteam.medchatbootservice.dtos.responses.ChatResponse;

public interface ChatbotService {
    ChatResponse reply(ChatRequest request);
}
