package com.famhealth.vault.service;

import com.famhealth.vault.dto.response.DocumentResponse;
import com.famhealth.vault.entity.Document;
import com.famhealth.vault.entity.DocumentType;
import com.famhealth.vault.entity.Patient;
import com.famhealth.vault.exception.ResourceNotFoundException;
import com.famhealth.vault.repository.DocumentRepository;
import com.famhealth.vault.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final S3StorageService s3StorageService;

    public DocumentService(
            DocumentRepository documentRepository,
            PatientRepository patientRepository,
            S3StorageService s3StorageService
    ) {
        this.documentRepository = documentRepository;
        this.patientRepository = patientRepository;
        this.s3StorageService = s3StorageService;
    }

    @Transactional
    public DocumentResponse uploadDocument(
            UUID patientId,
            DocumentType documentType,
            List<String> tags,
            MultipartFile file
    ) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        S3StorageService.S3UploadResult uploadResult = s3StorageService.uploadFile(
                file,
                "patients/" + patientId + "/documents"
        );

        Document document = new Document(
                patient,
                documentType,
                uploadResult.getFileUrl(),
                uploadResult.getKey(),
                file.getOriginalFilename(),
                LocalDateTime.now(),
                tags
        );

        Document savedDoc = documentRepository.save(document);
        return mapToResponse(savedDoc);
    }

    @Transactional
    public void deleteDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        s3StorageService.deleteFile(document.getS3Key());
        documentRepository.delete(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByPatient(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return documentRepository.findByPatientIdOrderByUploadDateDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DocumentResponse mapToResponse(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getPatient().getId(),
                doc.getDocumentType(),
                doc.getFileUrl(),
                doc.getFileName(),
                doc.getUploadDate(),
                doc.getTags()
        );
    }
}
