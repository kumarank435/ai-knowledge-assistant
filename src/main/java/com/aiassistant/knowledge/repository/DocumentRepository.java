package com.aiassistant.knowledge.repository;

import com.aiassistant.knowledge.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}