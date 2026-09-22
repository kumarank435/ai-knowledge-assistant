package com.aiassistant.knowledge.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfTextExtractorService {

    public String extractText(MultipartFile file) throws IOException {

        byte[] pdfBytes = file.getBytes();

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            PDFTextStripper pdfTextStripper = new PDFTextStripper();

            return pdfTextStripper.getText(document);
        }
    }
}