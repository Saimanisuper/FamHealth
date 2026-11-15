package com.famhealth.vault.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class SharedLinkResponse {

    private UUID id;
    private UUID patientId;
    private UUID uuidToken;
    private String shareUrl;
    private LocalDateTime expiresAt;
    private boolean active;

    public SharedLinkResponse() {
    }

    public SharedLinkResponse(UUID id, UUID patientId, UUID uuidToken, String shareUrl, LocalDateTime expiresAt, boolean active) {
        this.id = id;
        this.patientId = patientId;
        this.uuidToken = uuidToken;
        this.shareUrl = shareUrl;
        this.expiresAt = expiresAt;
        this.active = active;
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

    public UUID getUuidToken() {
        return uuidToken;
    }

    public void setUuidToken(UUID uuidToken) {
        this.uuidToken = uuidToken;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
