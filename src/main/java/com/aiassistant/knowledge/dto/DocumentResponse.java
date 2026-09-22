package com.aiassistant.knowledge.dto;

public class DocumentResponse {

    private Long id;
    private String title;
    private String fileName;
    private String content;

    public DocumentResponse() {
    }

    public DocumentResponse(Long id, String title, String fileName, String content) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }
}