package com.famhealth.vault.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class PatientResponse {

    private UUID id;
    private String name;
    private Integer age;
    private String bloodGroup;
    private String allergies;
    private LocalDateTime createdAt;

    public PatientResponse() {
    }

    public PatientResponse(UUID id, String name, Integer age, String bloodGroup, String allergies, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
