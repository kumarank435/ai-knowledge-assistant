package com.aiassistant.knowledge.service;
import com.aiassistant.knowledge.exception.DocumentNotFoundException;
import com.aiassistant.knowledge.entity.Document;
import com.aiassistant.knowledge.repository.DocumentRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    public Document getDocumentById(Long id) {
    return documentRepository
            .findById(id)
            .orElseThrow(() -> new DocumentNotFoundException(id));
            }
    public void deleteDocument(Long id) {

    Document document = documentRepository
            .findById(id)
            .orElseThrow(() -> new DocumentNotFoundException(id));

    documentRepository.delete(document);
}
public Document updateDocument(Long id, Document updatedDocument) {

    Document existingDocument = documentRepository
            .findById(id)
            .orElseThrow(() -> new DocumentNotFoundException(id));

    existingDocument.setTitle(updatedDocument.getTitle());
    existingDocument.setFileName(updatedDocument.getFileName());
    existingDocument.setContent(updatedDocument.getContent());

    return documentRepository.save(existingDocument);
}
}