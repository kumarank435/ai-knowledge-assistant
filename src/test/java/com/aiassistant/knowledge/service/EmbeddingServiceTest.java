package com.aiassistant.knowledge.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EmbeddingServiceTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void shouldGenerateEmbedding() {

        float[] embedding =
                embeddingService.generateEmbedding(
                        "Spring Boot is a Java framework"
                );

        assertNotNull(embedding);
        assertTrue(embedding.length > 0);
    }
}