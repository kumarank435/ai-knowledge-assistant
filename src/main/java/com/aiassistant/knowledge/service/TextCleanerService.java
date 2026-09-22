package com.aiassistant.knowledge.service;

import org.springframework.stereotype.Service;

@Service
public class TextCleanerService {

    public String cleanText(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        return text
                // Convert Windows line endings to normal line endings
                .replace("\r\n", "\n")

                // Remove excessive spaces and tabs
                .replaceAll("[ \t]+", " ")

                // Remove spaces/tabs at the beginning of lines
                .replaceAll("(?m)^[ \t]+", "")

                // Remove spaces/tabs at the end of lines
                .replaceAll("(?m)[ \t]+$", "")

                // Keep at most one blank line between paragraphs
                .replaceAll("\n{3,}", "\n\n")

                .trim();
    }
}