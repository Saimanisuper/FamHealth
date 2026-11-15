package com.famhealth.vault.repository;

import com.famhealth.vault.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByPatientId(UUID patientId);
    List<Document> findByPatientIdOrderByUploadDateDesc(UUID patientId);
}
