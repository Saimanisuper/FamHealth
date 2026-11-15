package com.famhealth.vault.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class MedicationRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @Min(value = 0, message = "Total strip count cannot be negative")
    private Integer totalStripCount;

    @NotNull(message = "Current pill count is required")
    @Min(value = 0, message = "Current pill count cannot be negative")
    private Integer currentPillCount;

    @NotEmpty(message = "At least one reminder time is required")
    private List<LocalTime> reminderTimes;

    public MedicationRequest() {
    }

    public MedicationRequest(UUID patientId, String medicineName, String dosage, Integer totalStripCount, Integer currentPillCount, List<LocalTime> reminderTimes) {
        this.patientId = patientId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.totalStripCount = totalStripCount;
        this.currentPillCount = currentPillCount;
        this.reminderTimes = reminderTimes;
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
}
