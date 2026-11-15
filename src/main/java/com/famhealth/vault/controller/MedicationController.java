package com.famhealth.vault.controller;

import com.famhealth.vault.dto.request.MedicationRequest;
import com.famhealth.vault.dto.response.ApiResponse;
import com.famhealth.vault.dto.response.MedicationResponse;
import com.famhealth.vault.service.MedicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * Add new medication schedule for a patient.
     * Accessible by ADMIN and VIEWER (NURSE is restricted).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<MedicationResponse>> addMedication(@Valid @RequestBody MedicationRequest request) {
        MedicationResponse response = medicationService.createMedication(request);
        return new ResponseEntity<>(ApiResponse.success("Medication schedule created successfully", response), HttpStatus.CREATED);
    }

    /**
     * List all medications scheduled for a patient.
     */
    @GetMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getMedications(@PathVariable("patientId") UUID patientId) {
        List<MedicationResponse> medications = medicationService.getMedicationsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(medications));
    }

    /**
     * Record taking a dose: decrements pill count by 1.
     */
    @PatchMapping("/{id}/consume")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER', 'NURSE')")
    public ResponseEntity<ApiResponse<MedicationResponse>> consumeDose(@PathVariable("id") UUID id) {
        MedicationResponse response = medicationService.consumeDose(id);
        return ResponseEntity.ok(ApiResponse.success("1 pill recorded as taken", response));
    }

    /**
     * Refill medication inventory with a new batch/strip of pills.
     */
    @PatchMapping("/{id}/refill")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<MedicationResponse>> refillStock(
            @PathVariable("id") UUID id,
            @RequestParam(value = "pills", defaultValue = "30") int pills
    ) {
        MedicationResponse response = medicationService.refillStock(id, pills);
        return ResponseEntity.ok(ApiResponse.success("Medication inventory refilled by +" + pills + " pills", response));
    }
}
