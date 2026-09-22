package com.aiassistant.knowledge.dto;

public class DocumentUploadResponse {

    private String fileName;
    private String message;

    public DocumentUploadResponse(String fileName, String message) {
        this.fileName = fileName;
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMessage() {
        return message;
    }
}