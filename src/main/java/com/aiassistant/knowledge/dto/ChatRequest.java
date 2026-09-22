package com.aiassistant.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ChatRequest {

    @NotBlank
    private String question;

    @NotEmpty
    private List<Long> documentIds;

    public ChatRequest() {
    }

    public ChatRequest(
            String question,
            List<Long> documentIds) {

        this.question = question;
        this.documentIds = documentIds;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }
}