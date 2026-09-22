package com.aiassistant.knowledge.controller;

import com.aiassistant.knowledge.dto.DocumentRequest;
import com.aiassistant.knowledge.dto.DocumentResponse;
import com.aiassistant.knowledge.dto.DocumentUploadResponse;
import com.aiassistant.knowledge.entity.Document;
import com.aiassistant.knowledge.service.DocumentService;
import com.aiassistant.knowledge.service.PdfTextExtractorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.aiassistant.knowledge.service.DocumentIngestionService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final PdfTextExtractorService pdfTextExtractorService;

     public DocumentController(
        DocumentService documentService,
        PdfTextExtractorService pdfTextExtractorService,
        DocumentIngestionService documentIngestionService) {

    this.documentService = documentService;
    this.pdfTextExtractorService = pdfTextExtractorService;
    this.documentIngestionService = documentIngestionService;
}

    @GetMapping
    public List<DocumentResponse> getAllDocuments() {

        return documentService.getAllDocuments()
                .stream()
                .map(document -> new DocumentResponse(
                        document.getId(),
                        document.getTitle(),
                        document.getFileName(),
                        document.getContent()
                ))
                .toList();
    }

    @PostMapping
    public DocumentResponse createDocument(
            @Valid @RequestBody DocumentRequest request) {

        Document document = new Document(
                request.getTitle(),
                request.getFileName(),
                request.getContent()
        );

        Document savedDocument = documentService.saveDocument(document);

        return new DocumentResponse(
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getFileName(),
                savedDocument.getContent()
        );
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocumentById(@PathVariable Long id) {

        Document document = documentService.getDocumentById(id);

        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getFileName(),
                document.getContent()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
    }

    @PutMapping("/{id}")
    public DocumentResponse updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequest request) {

        Document updatedDocument = new Document(
                request.getTitle(),
                request.getFileName(),
                request.getContent()
        );

        Document savedDocument = documentService.updateDocument(
                id,
                updatedDocument
        );

        return new DocumentResponse(
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getFileName(),
                savedDocument.getContent()
        );
    }

 @PostMapping("/upload")
public DocumentUploadResponse uploadDocument(
        @RequestParam("file") MultipartFile file) throws IOException {

    String extractedText =
            pdfTextExtractorService.extractText(file);

    Document document = new Document(
            file.getOriginalFilename(),
            file.getOriginalFilename(),
            extractedText
    );

    Document savedDocument =
            documentService.saveDocument(document);

    int chunkCount =
            documentIngestionService.ingestDocument(
                    savedDocument.getId(),
                    savedDocument.getFileName(),
                    savedDocument.getContent()
            );

    return new DocumentUploadResponse(
            savedDocument.getFileName(),
            "PDF uploaded, text extracted, and "
                    + chunkCount
                    + " chunks indexed successfully"
    );
}
    private final DocumentIngestionService documentIngestionService;
  
}