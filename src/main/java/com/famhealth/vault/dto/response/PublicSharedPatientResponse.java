package com.famhealth.vault.dto.response;

import java.util.List;
import java.util.UUID;

public class PublicSharedPatientResponse {

    private UUID patientId;
    private String patientName;
    private Integer age;
    private String bloodGroup;
    private String allergies;
    private List<DocumentResponse> documents;

    public PublicSharedPatientResponse() {
    }

    public PublicSharedPatientResponse(UUID patientId, String patientName, Integer age, String bloodGroup, String allergies, List<DocumentResponse> documents) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.documents = documents;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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

    public List<DocumentResponse> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentResponse> documents) {
        this.documents = documents;
    }
}
