package com.famhealth.vault.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class VitalsLogResponse {

    private UUID id;
    private UUID patientId;
    private LocalDateTime date;
    private Integer bloodPressureSys;
    private Integer bloodPressureDia;
    private Double fastingSugar;
    private String notes;
    private LocalDateTime createdAt;

    public VitalsLogResponse() {
    }

    public VitalsLogResponse(UUID id, UUID patientId, LocalDateTime date, Integer bloodPressureSys, Integer bloodPressureDia, Double fastingSugar, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.date = date;
        this.bloodPressureSys = bloodPressureSys;
        this.bloodPressureDia = bloodPressureDia;
        this.fastingSugar = fastingSugar;
        this.notes = notes;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
