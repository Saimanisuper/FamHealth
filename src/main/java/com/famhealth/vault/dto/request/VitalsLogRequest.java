package com.famhealth.vault.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;
import java.util.UUID;

public class VitalsLogRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Log date is required")
    @PastOrPresent(message = "Log date cannot be in the future")
    private LocalDateTime date;

    private Integer bloodPressureSys;

    private Integer bloodPressureDia;

    private Double fastingSugar;

    private String notes;

    public VitalsLogRequest() {
    }

    public VitalsLogRequest(UUID patientId, LocalDateTime date, Integer bloodPressureSys, Integer bloodPressureDia, Double fastingSugar, String notes) {
        this.patientId = patientId;
        this.date = date;
        this.bloodPressureSys = bloodPressureSys;
        this.bloodPressureDia = bloodPressureDia;
        this.fastingSugar = fastingSugar;
        this.notes = notes;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getBloodPressureSys() {
        return bloodPressureSys;
    }

    public void setBloodPressureSys(Integer bloodPressureSys) {
        this.bloodPressureSys = bloodPressureSys;
    }

    public Integer getBloodPressureDia() {
        return bloodPressureDia;
    }

    public void setBloodPressureDia(Integer bloodPressureDia) {
        this.bloodPressureDia = bloodPressureDia;
    }

    public Double getFastingSugar() {
        return fastingSugar;
    }

    public void setFastingSugar(Double fastingSugar) {
        this.fastingSugar = fastingSugar;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
