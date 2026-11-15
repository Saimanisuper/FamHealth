package com.famhealth.vault.service;

import com.famhealth.vault.dto.request.MedicationRequest;
import com.famhealth.vault.dto.response.MedicationResponse;
import com.famhealth.vault.entity.Medication;
import com.famhealth.vault.entity.Patient;
import com.famhealth.vault.exception.ResourceNotFoundException;
import com.famhealth.vault.repository.MedicationRepository;
import com.famhealth.vault.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;

    public MedicationService(MedicationRepository medicationRepository, PatientRepository patientRepository) {
        this.medicationRepository = medicationRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public MedicationResponse createMedication(MedicationRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Medication medication = new Medication(
                patient,
                request.getMedicineName(),
                request.getDosage(),
                request.getTotalStripCount(),
                request.getCurrentPillCount(),
                request.getReminderTimes()
        );

        Medication saved = medicationRepository.save(medication);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MedicationResponse> getMedicationsByPatient(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId));
        }
        return medicationRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public MedicationResponse consumeDose(UUID medicationId) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + medicationId));

        if (medication.getCurrentPillCount() != null && medication.getCurrentPillCount() > 0) {
            medication.setCurrentPillCount(medication.getCurrentPillCount() - 1);
            Medication updated = medicationRepository.save(medication);
            return mapToResponse(updated);
        }
        return mapToResponse(medication);
    }

    @Transactional
    public MedicationResponse refillStock(UUID medicationId, int additionalPills) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + medicationId));

        int current = medication.getCurrentPillCount() != null ? medication.getCurrentPillCount() : 0;
        medication.setCurrentPillCount(current + additionalPills);
        if (medication.getTotalStripCount() != null) {
            medication.setTotalStripCount(medication.getTotalStripCount() + 1);
        }
        Medication updated = medicationRepository.save(medication);
        return mapToResponse(updated);
    }

    public MedicationResponse mapToResponse(Medication med) {
        return new MedicationResponse(
                med.getId(),
                med.getPatient().getId(),
                med.getMedicineName(),
                med.getDosage(),
                med.getTotalStripCount(),
                med.getCurrentPillCount(),
                med.getReminderTimes(),
                med.getCreatedAt()
        );
    }
}
