package com.aiassistant.knowledge.service;

import com.aiassistant.knowledge.entity.Document;
import com.aiassistant.knowledge.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Test
    void shouldSaveDocument() {

        Document document = new Document();

        document.setTitle("Test Document");
        document.setFileName("test.txt");
        document.setContent("This is a test document.");

        Document saved =
                documentService.saveDocument(document);

        assertNotNull(saved);
        assertNotNull(saved.getId());

        assertEquals(
                "Test Document",
                saved.getTitle()
        );

        documentRepository.deleteById(saved.getId());
    }

    @Test
    void shouldGetAllDocuments() {

        Document document = new Document();

        document.setTitle("Test Document");
        document.setFileName("test.txt");
        document.setContent("Testing document retrieval.");

        Document saved =
                documentService.saveDocument(document);

        List<Document> documents =
                documentService.getAllDocuments();

        assertNotNull(documents);

        assertTrue(
                documents.stream()
                        .anyMatch(d ->
                                d.getId().equals(saved.getId())
                        )
        );

        documentRepository.deleteById(saved.getId());
    }

    @Test
    void shouldGetDocumentById() {

        Document document = new Document();

        document.setTitle("Test Document");
        document.setFileName("test.txt");
        document.setContent("Testing document lookup.");

        Document saved =
                documentService.saveDocument(document);

        Document found =
                documentService.getDocumentById(saved.getId());

        assertNotNull(found);

        assertEquals(
                saved.getId(),
                found.getId()
        );

        documentRepository.deleteById(saved.getId());
    }
}