package com.aiassistant.knowledge.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateAnswer(String question) {

        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();
    }
}