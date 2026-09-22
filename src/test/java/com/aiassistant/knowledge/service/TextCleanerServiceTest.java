package com.aiassistant.knowledge.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextCleanerServiceTest {

    private final TextCleanerService textCleanerService =
            new TextCleanerService();

    @Test
    void shouldCleanExtractedText() {

        String input =
                "  Hello    Java\r\n\r\n\r\nSpring   Boot  ";

        String result =
                textCleanerService.cleanText(input);

        assertEquals(
                "Hello Java\n\nSpring Boot",
                result
        );
    }

    @Test
    void shouldReturnEmptyStringForBlankText() {

        String result =
                textCleanerService.cleanText("   ");

        assertEquals("", result);
    }
}