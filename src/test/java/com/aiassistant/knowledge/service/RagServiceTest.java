package com.aiassistant.knowledge.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RagServiceTest {

    @Test
    void shouldAnswerUsingRetrievedDocument() {

        RetrievalService retrievalService =
                mock(RetrievalService.class);

        ChatClient.Builder chatClientBuilder =
                mock(ChatClient.Builder.class);

        ChatClient chatClient =
                mock(ChatClient.class);

        when(chatClientBuilder.build())
                .thenReturn(chatClient);

        Document document =
                new Document(
                        "Kumaran knows Java, Python and C++.",
                        java.util.Map.of(
                                "fileName", "KumaranK.pdf",
                                "chunkIndex", 0
                        )
                );

        List<Long> documentIds =
                List.of(89L);

        String question =
                "What programming languages does Kumaran know?";

        when(
                retrievalService.retrieveFromDocuments(
                        question,
                        documentIds,
                        5
                )
        ).thenReturn(List.of(document));

        ChatClient.ChatClientRequestSpec requestSpec =
                mock(ChatClient.ChatClientRequestSpec.class);

        ChatClient.CallResponseSpec responseSpec =
                mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt())
                .thenReturn(requestSpec);

        when(requestSpec.user(anyString()))
                .thenReturn(requestSpec);

        when(requestSpec.call())
                .thenReturn(responseSpec);

        when(responseSpec.content())
                .thenReturn(
                        "Kumaran knows Java, Python and C++."
                );

        RagService ragService =
                new RagService(
                        retrievalService,
                        chatClientBuilder
                );

        String answer =
                ragService.ask(
                        question,
                        documentIds
                );

        assertNotNull(answer);

        verify(
                retrievalService
        ).retrieveFromDocuments(
                question,
                documentIds,
                5
        );
    }
}