package com.aiassistant.knowledge.dto;

import java.util.List;

public class ChatResponse {

    private String answer;
    private List<ChatSource> sources;

    public ChatResponse(
            String answer,
            List<ChatSource> sources) {

        this.answer = answer;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public List<ChatSource> getSources() {
        return sources;
    }
}