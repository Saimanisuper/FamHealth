package com.famhealth.vault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vitals_logs")
public class VitalsLog extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;

    @Column(name = "log_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "blood_pressure_sys")
    private Integer bloodPressureSys;

    @Column(name = "blood_pressure_dia")
    private Integer bloodPressureDia;

    @Column(name = "fasting_sugar")
    private Double fastingSugar;

    @Column(name = "notes", length = 1000)
    private String notes;

    public VitalsLog() {
    }

    public VitalsLog(Patient patient, LocalDateTime date, Integer bloodPressureSys, Integer bloodPressureDia, Double fastingSugar, String notes) {
        this.patient = patient;
        this.date = date;
        this.bloodPressureSys = bloodPressureSys;
        this.bloodPressureDia = bloodPressureDia;
        this.fastingSugar = fastingSugar;
        this.notes = notes;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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
