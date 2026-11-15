package com.famhealth.vault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shared_links")
public class SharedLink extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;

    @Column(name = "uuid_token", nullable = false, unique = true)
    private UUID uuidToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public SharedLink() {
    }

    public SharedLink(Patient patient, UUID uuidToken, LocalDateTime expiresAt) {
        this.patient = patient;
        this.uuidToken = uuidToken;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public UUID getUuidToken() {
        return uuidToken;
    }

    public void setUuidToken(UUID uuidToken) {
        this.uuidToken = uuidToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
