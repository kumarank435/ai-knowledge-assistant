package com.aiassistant.knowledge.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfTextExtractorServiceTest {

    private final PdfTextExtractorService pdfTextExtractorService =
            new PdfTextExtractorService();

    @Test
    void shouldExtractTextFromPdf() throws Exception {

        byte[] pdfBytes;

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                contentStream.beginText();

                PDType1Font font =
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Java Spring Boot");
                contentStream.endText();
            }

            java.io.ByteArrayOutputStream outputStream =
                    new java.io.ByteArrayOutputStream();

            document.save(outputStream);

            pdfBytes = outputStream.toByteArray();
        }

        MockMultipartFile pdfFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                pdfBytes
        );

        String extractedText =
                pdfTextExtractorService.extractText(pdfFile);

        assertTrue(extractedText.contains("Java Spring Boot"));
    }
}