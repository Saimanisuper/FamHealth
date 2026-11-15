package com.famhealth.vault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medications")
public class Medication extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;

    @Column(name = "medicine_name", nullable = false, length = 150)
    private String medicineName;

    @Column(name = "dosage", nullable = false, length = 100)
    private String dosage;

    @Column(name = "total_strip_count")
    private Integer totalStripCount;

    @Column(name = "current_pill_count", nullable = false)
    private Integer currentPillCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medication_reminder_times", joinColumns = @JoinColumn(name = "medication_id"))
    @Column(name = "reminder_time", nullable = false)
    private List<LocalTime> reminderTimes = new ArrayList<>();

    public Medication() {
    }

    public Medication(Patient patient, String medicineName, String dosage, Integer totalStripCount, Integer currentPillCount, List<LocalTime> reminderTimes) {
        this.patient = patient;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.totalStripCount = totalStripCount;
        this.currentPillCount = currentPillCount;
        if (reminderTimes != null) {
            this.reminderTimes = reminderTimes;
        }
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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
