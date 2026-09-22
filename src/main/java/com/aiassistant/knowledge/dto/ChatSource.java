package com.aiassistant.knowledge.dto;

public class ChatSource {

    private String fileName;
    private Integer chunkIndex;

    public ChatSource(String fileName, Integer chunkIndex) {
        this.fileName = fileName;
        this.chunkIndex = chunkIndex;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }
}