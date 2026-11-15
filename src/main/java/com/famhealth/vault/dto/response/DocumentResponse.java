package com.famhealth.vault.dto.response;

import com.famhealth.vault.entity.DocumentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DocumentResponse {

    private UUID id;
    private UUID patientId;
    private DocumentType documentType;
    private String fileUrl;
    private String fileName;
    private LocalDateTime uploadDate;
    private List<String> tags;

    public DocumentResponse() {
    }

    public DocumentResponse(UUID id, UUID patientId, DocumentType documentType, String fileUrl, String fileName, LocalDateTime uploadDate, List<String> tags) {
        this.id = id;
        this.patientId = patientId;
        this.documentType = documentType;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.uploadDate = uploadDate;
        this.tags = tags;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
