package com.aiassistant.knowledge.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Test
    void shouldGenerateAnswer() {

        String answer =
                chatService.generateAnswer(
                        "What is Java?"
                );

        assertNotNull(answer);
        assertFalse(answer.isBlank());

        System.out.println("LLM Answer: " + answer);
    }
}