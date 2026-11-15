package com.famhealth.vault.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PatientRequest {

    @NotBlank(message = "Patient name is required")
    private String name;

    @NotNull(message = "Patient age is required")
    @Min(value = 0, message = "Age cannot be negative")
    private Integer age;

    private String bloodGroup;

    private String allergies;

    public PatientRequest() {
    }

    public PatientRequest(String name, Integer age, String bloodGroup, String allergies) {
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
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
}
