package com.aiassistant.knowledge.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {

    private final TextCleanerService textCleanerService;
    private final TextChunkerService textChunkerService;
    private final VectorStore vectorStore;

    public DocumentIngestionService(
            TextCleanerService textCleanerService,
            TextChunkerService textChunkerService,
            VectorStore vectorStore) {

        this.textCleanerService = textCleanerService;
        this.textChunkerService = textChunkerService;
        this.vectorStore = vectorStore;
    }

    public int ingestDocument(
            Long documentId,
            String fileName,
            String content) {

        String cleanedText =
                textCleanerService.cleanText(content);

        List<String> chunks =
                textChunkerService.chunkText(cleanedText);

        List<Document> vectorDocuments = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {

            String chunk = chunks.get(i);

            Document document = new Document(
                    chunk,
                    Map.of(
                            "documentId", documentId,
                            "fileName", fileName,
                            "chunkIndex", i
                    )
            );

            vectorDocuments.add(document);
        }

        if (!vectorDocuments.isEmpty()) {
            vectorStore.add(vectorDocuments);
        }

        return vectorDocuments.size();
    }
}