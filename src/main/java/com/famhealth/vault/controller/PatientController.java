package com.famhealth.vault.controller;

import com.famhealth.vault.dto.request.PatientRequest;
import com.famhealth.vault.dto.response.ApiResponse;
import com.famhealth.vault.dto.response.PatientResponse;
import com.famhealth.vault.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.createPatient(request);
        return new ResponseEntity<>(ApiResponse.success("Patient created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER', 'NURSE')")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getMyPatients() {
        List<PatientResponse> patients = patientService.getMyPatients();
        return ResponseEntity.ok(ApiResponse.success(patients));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER', 'NURSE')")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable("id") UUID id) {
        PatientResponse response = patientService.getPatientById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
