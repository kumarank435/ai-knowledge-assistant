package com.aiassistant.knowledge;

import com.aiassistant.knowledge.service.RagService;
import com.aiassistant.knowledge.service.RetrievalService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RagEvaluationTest {

    @Autowired
    private RetrievalService retrievalService;

    @Autowired
    private RagService ragService;

    @Test
    void shouldRetrieveRelevantChunks() {

        Long documentId = 69L;

        String question =
                "What programming languages does Kumaran know?";

        List<Document> results =
                retrievalService.retrieveFromDocument(
                        question,
                        documentId,
                        3
                );

        assertFalse(
                results.isEmpty(),
                "RAG should retrieve at least one relevant chunk"
        );

        assertTrue(
                results.stream()
                        .anyMatch(document ->
                                document.getText()
                                        .toLowerCase()
                                        .contains("java")
                        ),
                "Retrieved chunks should contain relevant information"
        );

        assertTrue(
                results.stream()
                        .allMatch(document -> {

                            Object metadataDocumentId =
                                    document.getMetadata()
                                            .get("documentId");

                            return metadataDocumentId instanceof Number
                                    && ((Number) metadataDocumentId)
                                            .longValue()
                                            == documentId;
                        }),
                "All retrieved chunks should belong to the requested document"
        );
    }

    @Test
    void shouldGenerateRelevantAnswer() {

        Long documentId = 69L;

        String question =
                "What programming languages does Kumaran know?";

        String answer =
                ragService.ask(
                        question,
                        documentId
                );

        System.out.println();
        System.out.println("========== ANSWER QUALITY ==========");
        System.out.println("Question: " + question);
        System.out.println("Answer: " + answer);
        System.out.println("====================================");

        assertNotNull(
                answer,
                "RAG answer should not be null"
        );

        assertFalse(
                answer.isBlank(),
                "RAG answer should not be empty"
        );

        String lowerCaseAnswer =
                answer.toLowerCase();

        assertTrue(
                lowerCaseAnswer.contains("java"),
                "Answer should mention Java"
        );

        assertTrue(
                lowerCaseAnswer.contains("python"),
                "Answer should mention Python"
        );
    }

    @Test
    void shouldAvoidHallucinationForUnknownQuestion() {

        Long documentId = 69L;

        String question =
                "What is Kumaran's favorite food?";

        String answer =
                ragService.ask(
                        question,
                        documentId
                );

        System.out.println();
        System.out.println("========== HALLUCINATION TEST ==========");
        System.out.println("Question: " + question);
        System.out.println("Answer: " + answer);
        System.out.println("=========================================");

        assertNotNull(
                answer,
                "RAG answer should not be null"
        );

        assertFalse(
                answer.isBlank(),
                "RAG answer should not be empty"
        );

        assertTrue(
                answer.toLowerCase().contains("could not find"),
                "RAG should refuse to answer when information is not present"
        );
    }
}