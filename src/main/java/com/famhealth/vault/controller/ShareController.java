package com.famhealth.vault.controller;

import com.famhealth.vault.dto.response.ApiResponse;
import com.famhealth.vault.dto.response.SharedLinkResponse;
import com.famhealth.vault.service.SharedLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private final SharedLinkService sharedLinkService;

    public ShareController(SharedLinkService sharedLinkService) {
        this.sharedLinkService = sharedLinkService;
    }

    /**
     * Generates a 24-hour time-limited access UUID link for sharing patient documents.
     * Accessible by ADMIN and VIEWER (NURSE is restricted).
     */
    @PostMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    public ResponseEntity<ApiResponse<SharedLinkResponse>> generateShareLink(@PathVariable("patientId") UUID patientId) {
        SharedLinkResponse response = sharedLinkService.create24HourShareLink(patientId);
        return new ResponseEntity<>(ApiResponse.success("24-hour share link created successfully", response), HttpStatus.CREATED);
    }
}
