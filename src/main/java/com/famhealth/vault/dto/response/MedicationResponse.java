package com.famhealth.vault.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class MedicationResponse {

    private UUID id;
    private UUID patientId;
    private String medicineName;
    private String dosage;
    private Integer totalStripCount;
    private Integer currentPillCount;
    private List<LocalTime> reminderTimes;
    private LocalDateTime createdAt;

    public MedicationResponse() {
    }

    public MedicationResponse(UUID id, UUID patientId, String medicineName, String dosage, Integer totalStripCount, Integer currentPillCount, List<LocalTime> reminderTimes, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.totalStripCount = totalStripCount;
        this.currentPillCount = currentPillCount;
        this.reminderTimes = reminderTimes;
        this.createdAt = createdAt;
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

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public Integer getTotalStripCount() {
        return totalStripCount;
    }

    public void setTotalStripCount(Integer totalStripCount) {
        this.totalStripCount = totalStripCount;
    }

    public Integer getCurrentPillCount() {
        return currentPillCount;
    }

    public void setCurrentPillCount(Integer currentPillCount) {
        this.currentPillCount = currentPillCount;
    }

    public List<LocalTime> getReminderTimes() {
        return reminderTimes;
    }

    public void setReminderTimes(List<LocalTime> reminderTimes) {
        this.reminderTimes = reminderTimes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
