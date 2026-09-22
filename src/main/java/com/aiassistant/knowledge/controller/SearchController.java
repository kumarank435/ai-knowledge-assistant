package com.aiassistant.knowledge.controller;

import com.aiassistant.knowledge.service.RetrievalService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final RetrievalService retrievalService;

    public SearchController(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @GetMapping
    public List<Document> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "3") int topK,
            @RequestParam(required = false) Long documentId) {

        if (documentId != null) {

            return retrievalService.retrieveFromDocument(
                    query,
                    documentId,
                    topK
            );
        }

        return retrievalService.retrieve(query, topK);
    }
}