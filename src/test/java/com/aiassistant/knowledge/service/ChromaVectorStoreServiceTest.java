package com.aiassistant.knowledge.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ChromaVectorStoreServiceTest {

    @Autowired
    private ChromaVectorStoreService chromaVectorStoreService;

    @Test
    void shouldStoreAndSearchDocument() {

        chromaVectorStoreService.addDocument(
                "Spring Boot is a Java framework used to build backend applications.",
                Map.of(
                        "source", "test",
                        "type", "technology"
                )
        );

        List<Document> results =
                chromaVectorStoreService.search(
                        "Java backend framework",
                        1
                );

        assertFalse(results.isEmpty());

        assertTrue(
                results.get(0)
                        .getText()
                        .contains("Spring Boot")
        );
    }
}