package com.aiassistant.knowledge.controller;

import com.aiassistant.knowledge.dto.ChatRequest;
import com.aiassistant.knowledge.dto.ChatResponse;
import com.aiassistant.knowledge.dto.ChatSource;
import com.aiassistant.knowledge.service.RagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RagService ragService;

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public ChatResponse chat(
            @Valid @RequestBody ChatRequest request) {

        String answer =
                ragService.ask(
                        request.getQuestion(),
                        request.getDocumentIds()
                );

        List<ChatSource> sources =
                ragService.getSources(
                        request.getQuestion(),
                        request.getDocumentIds()
                );

        return new ChatResponse(
                answer,
                sources
        );
    }
}