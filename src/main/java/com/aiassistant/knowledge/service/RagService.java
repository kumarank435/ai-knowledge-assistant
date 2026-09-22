package com.aiassistant.knowledge.service;

import com.aiassistant.knowledge.dto.ChatSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final RetrievalService retrievalService;
    private final ChatClient chatClient;

    private static final int TOP_K = 5;

    public RagService(
            RetrievalService retrievalService,
            ChatClient.Builder chatClientBuilder) {

        this.retrievalService = retrievalService;
        this.chatClient = chatClientBuilder.build();
    }

    // ============================================================
    // MULTI-DOCUMENT RAG
    // ============================================================

    public String ask(
            String question,
            List<Long> documentIds) {

        List<Document> documents =
                retrievalService.retrieveFromDocuments(
                        question,
                        documentIds,
                        TOP_K
                );

        if (documents.isEmpty()) {
            return "I could not find the answer in the selected documents.";
        }

        StringBuilder context =
                new StringBuilder();

        for (int i = 0; i < documents.size(); i++) {

            Document document =
                    documents.get(i);

            context
                    .append("SOURCE ")
                    .append(i + 1)
                    .append(":\n")
                    .append("Document: ")
                    .append(document
                            .getMetadata()
                            .get("fileName"))
                    .append("\n")
                    .append(document.getText())
                    .append("\n\n");
        }

        String prompt = """
                You are an AI assistant that answers questions
                using the user's selected documents.

                Use ONLY the information provided in the
                retrieved context.

                IMPORTANT RULES:

                1. Read ALL provided sources before answering.

                2. The sources may come from multiple documents.
                   Combine information from different documents
                   when necessary.

                3. Do not assume that information from one document
                   represents the entire knowledge base.

                4. If the question asks about multiple topics,
                   carefully check ALL sources before answering.

                5. If the answer requires information from more than
                   one document, combine the relevant information.

                6. Do not invent or guess information.

                7. If the answer is not present in the retrieved
                   context, say exactly:

                   "I could not find the answer in the selected documents."

                8. Give a clear and direct answer.

                Retrieved Context:

                %s

                Question:

                %s

                Answer:
                """.formatted(
                context.toString(),
                question
        );

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    // ============================================================
    // BACKWARD COMPATIBILITY
    // Allows existing code/tests using one document to continue
    // working.
    // ============================================================

    public String ask(
            String question,
            Long documentId) {

        return ask(
                question,
                List.of(documentId)
        );
    }

    // ============================================================
    // MULTI-DOCUMENT SOURCES
    // ============================================================

    public List<ChatSource> getSources(
            String question,
            List<Long> documentIds) {

        List<Document> documents =
                retrievalService.retrieveFromDocuments(
                        question,
                        documentIds,
                        TOP_K
                );

        return documents.stream()
                .map(document ->
                        new ChatSource(
                                (String) document
                                        .getMetadata()
                                        .get("fileName"),

                                ((Number) document
                                        .getMetadata()
                                        .get("chunkIndex"))
                                        .intValue()
                        )
                )
                .toList();
    }

    // ============================================================
    // BACKWARD COMPATIBILITY FOR SOURCES
    // ============================================================

    public List<ChatSource> getSources(
            String question,
            Long documentId) {

        return getSources(
                question,
                List.of(documentId)
        );
    }
}