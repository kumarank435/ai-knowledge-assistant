package com.aiassistant.knowledge.dto;

import jakarta.validation.constraints.NotBlank;

public class DocumentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Content is required")
    private String content;

    public DocumentRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}