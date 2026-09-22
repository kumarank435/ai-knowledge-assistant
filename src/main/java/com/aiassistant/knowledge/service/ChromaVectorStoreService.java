package com.aiassistant.knowledge.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChromaVectorStoreService {

    private final VectorStore vectorStore;

    public ChromaVectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addDocument(String content, Map<String, Object> metadata) {

        Document document = new Document(
                content,
                metadata
        );

        vectorStore.add(List.of(document));
    }

    public List<Document> search(String query, int topK) {

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        return vectorStore.similaritySearch(request);
    }
}