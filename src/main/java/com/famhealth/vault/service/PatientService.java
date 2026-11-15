package com.famhealth.vault.service;

import com.famhealth.vault.dto.request.PatientRequest;
import com.famhealth.vault.dto.response.PatientResponse;
import com.famhealth.vault.entity.Patient;
import com.famhealth.vault.entity.User;
import com.famhealth.vault.exception.ResourceNotFoundException;
import com.famhealth.vault.repository.PatientRepository;
import com.famhealth.vault.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + currentUsername));

        Patient patient = new Patient(
                request.getName(),
                request.getAge(),
                request.getBloodGroup(),
                request.getAllergies(),
                currentUser
        );

        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getMyPatients() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + currentUsername));

        if (currentUser.getRole() == com.famhealth.vault.entity.Role.NURSE) {
            return patientRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return patientRepository.findByUser(currentUser).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return mapToResponse(patient);
    }

    private PatientResponse mapToResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getBloodGroup(),
                patient.getAllergies(),
                patient.getCreatedAt()
        );
    }
}
