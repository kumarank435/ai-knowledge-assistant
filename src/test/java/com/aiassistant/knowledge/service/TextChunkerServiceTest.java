package com.aiassistant.knowledge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TextChunkerServiceTest {

    private TextChunkerService textChunkerService;

    @BeforeEach
    void setUp() {

        EmbeddingModel embeddingModel =
                org.mockito.Mockito.mock(
                        EmbeddingModel.class
                );

        /*
         * Return the same embedding for the test.
         *
         * This allows the test to verify that related
         * paragraphs can be grouped into semantic chunks
         * without requiring Ollama during the unit test.
         */
        when(
                embeddingModel.embed(anyString())
        ).thenReturn(
                new float[]{
                        1.0f,
                        0.5f,
                        0.2f
                }
        );

        textChunkerService =
                new TextChunkerService(
                        embeddingModel
                );
    }

    @Test
    void shouldReturnEmptyListForEmptyText() {

        var chunks =
                textChunkerService.chunkText("");

        assertTrue(chunks.isEmpty());
    }

    @Test
    void shouldCreateChunkForSimpleText() {

        String text =
                "Java is a programming language.";

        var chunks =
                textChunkerService.chunkText(text);

        assertFalse(chunks.isEmpty());

        assertEquals(
                "Java is a programming language.",
                chunks.get(0)
        );
    }

    @Test
    void shouldGroupRelatedParagraphs() {

        String text = """
                Java is a programming language.
                
                Java is widely used for backend development.
                
                Spring Boot is a Java framework used to build backend applications.
                """;

        var chunks =
                textChunkerService.chunkText(text);

        assertFalse(chunks.isEmpty());

        String combined =
                String.join(
                        "\n",
                        chunks
                );

        assertTrue(
                combined.contains("Java is a programming language.")
        );

        assertTrue(
                combined.contains(
                        "Java is widely used for backend development."
                )
        );

        assertTrue(
                combined.contains(
                        "Spring Boot is a Java framework used to build backend applications."
                )
        );
    }

    @Test
    void shouldHandleMultipleParagraphs() {

        String text = """
                Kumaran is an AI and Data Science student.
                
                He knows Java and Python.
                
                He has worked on several AI projects.
                
                He has experience with Spring Boot and React.
                """;

        var chunks =
                textChunkerService.chunkText(text);

        assertFalse(chunks.isEmpty());

        assertTrue(
                chunks.size() >= 1
        );
    }

    @Test
    void shouldNotCreateChunkLargerThanMaximumSize() {

        String text =
                "A".repeat(3000);

        var chunks =
                textChunkerService.chunkText(text);

        assertFalse(chunks.isEmpty());

        for (String chunk : chunks) {

            assertTrue(
                    chunk.length() <= 1200,
                    "Chunk exceeds maximum size: "
                            + chunk.length()
            );
        }
    }
}