package com.aiassistant.knowledge.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RetrievalService {

    private final VectorStore vectorStore;

    public RetrievalService(
            VectorStore vectorStore) {

        this.vectorStore = vectorStore;
    }

    public List<Document> retrieve(
            String query,
            int topK) {

        SearchRequest searchRequest =
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .build();

        return vectorStore.similaritySearch(
                searchRequest
        );
    }

    public List<Document> retrieveFromDocument(
            String query,
            Long documentId,
            int topK) {

        SearchRequest searchRequest =
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .filterExpression(
                                "documentId == " + documentId
                        )
                        .build();

        return vectorStore.similaritySearch(
                searchRequest
        );
    }

    public List<Document> retrieveFromDocuments(
            String query,
            List<Long> documentIds,
            int topK) {

        if (documentIds == null || documentIds.isEmpty()) {
            return List.of();
        }

        String filterExpression =
                documentIds.stream()
                        .map(id -> "documentId == " + id)
                        .collect(Collectors.joining(" OR "));

        SearchRequest searchRequest =
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .filterExpression(filterExpression)
                        .build();

        return vectorStore.similaritySearch(
                searchRequest
        );
    }
}