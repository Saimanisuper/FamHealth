package com.famhealth.vault.controller;

import com.famhealth.vault.dto.request.VitalsLogRequest;
import com.famhealth.vault.dto.response.ApiResponse;
import com.famhealth.vault.dto.response.VitalsLogResponse;
import com.famhealth.vault.service.VitalsService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vitals")
public class VitalsController {

    private final VitalsService vitalsService;

    public VitalsController(VitalsService vitalsService) {
        this.vitalsService = vitalsService;
    }

    /**
     * Method-level Security: ROLE_NURSE and ROLE_ADMIN can POST daily vitals logs.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public ResponseEntity<ApiResponse<VitalsLogResponse>> logVitals(@Valid @RequestBody VitalsLogRequest request) {
        VitalsLogResponse response = vitalsService.logVitals(request);
        return new ResponseEntity<>(ApiResponse.success("Vitals logged successfully", response), HttpStatus.CREATED);
    }

    /**
     * Fetch vitals for a patient within an optional date range.
     * Restricted to ROLE_ADMIN and ROLE_VIEWER (ROLE_NURSE cannot read historical patient logs).
     */
    @GetMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<List<VitalsLogResponse>>> getVitals(
            @PathVariable("patientId") UUID patientId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<VitalsLogResponse> vitals = vitalsService.getVitals(patientId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(vitals));
    }
}
