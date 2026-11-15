package com.famhealth.vault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient extends BaseAuditableEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "allergies", length = 500)
    private String allergies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<VitalsLog> vitalsLogs = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Medication> medications = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SharedLink> sharedLinks = new ArrayList<>();

    public Patient() {
    }

    public Patient(String name, Integer age, String bloodGroup, String allergies, User user) {
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<VitalsLog> getVitalsLogs() {
        return vitalsLogs;
    }

    public void setVitalsLogs(List<VitalsLog> vitalsLogs) {
        this.vitalsLogs = vitalsLogs;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public List<SharedLink> getSharedLinks() {
        return sharedLinks;
    }

    public void setSharedLinks(List<SharedLink> sharedLinks) {
        this.sharedLinks = sharedLinks;
    }
}
