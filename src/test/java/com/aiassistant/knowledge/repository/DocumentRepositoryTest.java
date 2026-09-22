package com.aiassistant.knowledge.repository;

import com.aiassistant.knowledge.entity.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

   @Test
void shouldSaveAndFindDocument() {

    Document document = new Document(
            "Java Basics",
            "java-basics.txt",
            "This is a Java basics document."
    );

    Document savedDocument = documentRepository.save(document);

    assertNotNull(savedDocument.getId());

    Document foundDocument = documentRepository
            .findById(savedDocument.getId())
            .orElseThrow();

    assertEquals("Java Basics", foundDocument.getTitle());
    assertEquals("java-basics.txt", foundDocument.getFileName());
    assertEquals(
            "This is a Java basics document.",
            foundDocument.getContent()
    );
}
}