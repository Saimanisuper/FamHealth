package com.famhealth.vault.service;

import com.famhealth.vault.dto.response.DocumentResponse;
import com.famhealth.vault.dto.response.PublicSharedPatientResponse;
import com.famhealth.vault.dto.response.SharedLinkResponse;
import com.famhealth.vault.entity.Patient;
import com.famhealth.vault.entity.SharedLink;
import com.famhealth.vault.exception.ResourceNotFoundException;
import com.famhealth.vault.exception.TokenExpiredException;
import com.famhealth.vault.repository.DocumentRepository;
import com.famhealth.vault.repository.PatientRepository;
import com.famhealth.vault.repository.SharedLinkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SharedLinkService {

    private final SharedLinkRepository sharedLinkRepository;
    private final PatientRepository patientRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public SharedLinkService(
            SharedLinkRepository sharedLinkRepository,
            PatientRepository patientRepository,
            DocumentRepository documentRepository,
            DocumentService documentService
    ) {
        this.sharedLinkRepository = sharedLinkRepository;
        this.patientRepository = patientRepository;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
    }

    @Transactional
    public SharedLinkResponse create24HourShareLink(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        UUID token = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        SharedLink sharedLink = new SharedLink(patient, token, expiresAt);
        SharedLink saved = sharedLinkRepository.save(sharedLink);

        String shareUrl = "/api/public/share/" + token;

        return new SharedLinkResponse(
                saved.getId(),
                patient.getId(),
                token,
                shareUrl,
                expiresAt,
                !saved.isExpired()
        );
    }

    @Transactional(readOnly = true)
    public PublicSharedPatientResponse getSharedPatientDocuments(UUID uuidToken) {
        return getPatientDocumentsByShareToken(uuidToken);
    }

    @Transactional(readOnly = true)
    public PublicSharedPatientResponse getPatientDocumentsByShareToken(UUID uuidToken) {
        SharedLink sharedLink = sharedLinkRepository.findByUuidToken(uuidToken)
                .orElseThrow(() -> new ResourceNotFoundException("Shared link was not found or is invalid"));

        if (sharedLink.isExpired()) {
            throw new TokenExpiredException(
                    "The shared link has expired on " + sharedLink.getExpiresAt() + ". Please ask the family administrator for a fresh link."
            );
        }

        Patient patient = sharedLink.getPatient();
        List<DocumentResponse> documents = documentRepository.findByPatientIdOrderByUploadDateDesc(patient.getId())
                .stream()
                .map(documentService::mapToResponse)
                .toList();

        return new PublicSharedPatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getBloodGroup(),
                patient.getAllergies(),
                documents
        );
    }
}
