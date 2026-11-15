package com.famhealth.vault.controller;

import com.famhealth.vault.dto.response.ApiResponse;
import com.famhealth.vault.dto.response.DocumentResponse;
import com.famhealth.vault.entity.DocumentType;
import com.famhealth.vault.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Upload medical record to AWS S3 and save metadata in PostgreSQL.
     * Accessible by ADMIN and VIEWER (NURSE is restricted).
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("patientId") UUID patientId,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "tags", required = false) List<String> tags
    ) {
        DocumentResponse response = documentService.uploadDocument(patientId, documentType, tags, file);
        return new ResponseEntity<>(ApiResponse.success("Document uploaded successfully to S3", response), HttpStatus.CREATED);
    }

    /**
     * Method-level Security: Only ROLE_ADMIN can delete medical documents.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable("id") UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully from S3 and database", null));
    }

    /**
     * Fetch patient's documents.
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByPatient(@PathVariable("patientId") UUID patientId) {
        List<DocumentResponse> documents = documentService.getDocumentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }
}
