package com.aiassistant.knowledge.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DocumentIngestionServiceTest {

    @Autowired
    private DocumentIngestionService documentIngestionService;

    @Autowired
    private VectorStore vectorStore;

    @Test
    void shouldIngestDocumentIntoChroma() {

        String content = """
                Spring Boot is a Java framework.
                Java is widely used for backend development.
                Spring Boot makes it easier to build REST APIs.
                """;

        int chunkCount =
                documentIngestionService.ingestDocument(
                        999L,
                        "test-document.txt",
                        content
                );

        assertTrue(chunkCount > 0);

        List<Document> results =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query("Java backend framework")
                                .topK(1)
                                .build()
                );

        assertFalse(results.isEmpty());

        assertTrue(
                results.get(0)
                        .getText()
                        .contains("Spring Boot")
        );
    }
}