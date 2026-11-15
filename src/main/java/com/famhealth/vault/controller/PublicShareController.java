package com.famhealth.vault.controller;

import com.famhealth.vault.dto.response.ApiResponse;
import com.famhealth.vault.dto.response.PublicSharedPatientResponse;
import com.famhealth.vault.service.SharedLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/share")
public class PublicShareController {

    private final SharedLinkService sharedLinkService;

    public PublicShareController(SharedLinkService sharedLinkService) {
        this.sharedLinkService = sharedLinkService;
    }

    /**
     * Public access endpoint to view patient records using a temporary shared token.
     * Validates that the token exists and expiresAt > now.
     */
    @GetMapping("/{uuidToken}")
    public ResponseEntity<ApiResponse<PublicSharedPatientResponse>> viewSharedPatientRecords(
            @PathVariable("uuidToken") UUID uuidToken
    ) {
        PublicSharedPatientResponse response = sharedLinkService.getPatientDocumentsByShareToken(uuidToken);
        return ResponseEntity.ok(ApiResponse.success("Shared patient records retrieved successfully", response));
    }
}
