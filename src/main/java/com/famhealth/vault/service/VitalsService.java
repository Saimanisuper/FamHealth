package com.famhealth.vault.service;

import com.famhealth.vault.dto.request.VitalsLogRequest;
import com.famhealth.vault.dto.response.VitalsLogResponse;
import com.famhealth.vault.entity.Patient;
import com.famhealth.vault.entity.VitalsLog;
import com.famhealth.vault.exception.ResourceNotFoundException;
import com.famhealth.vault.repository.PatientRepository;
import com.famhealth.vault.repository.VitalsLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VitalsService {

    private final VitalsLogRepository vitalsLogRepository;
    private final PatientRepository patientRepository;

    public VitalsService(VitalsLogRepository vitalsLogRepository, PatientRepository patientRepository) {
        this.vitalsLogRepository = vitalsLogRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public VitalsLogResponse logVitals(VitalsLogRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        VitalsLog vitalsLog = new VitalsLog(
                patient,
                request.getDate() != null ? request.getDate() : LocalDateTime.now(),
                request.getBloodPressureSys(),
                request.getBloodPressureDia(),
                request.getFastingSugar(),
                request.getNotes()
        );

        VitalsLog saved = vitalsLogRepository.save(vitalsLog);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<VitalsLogResponse> getVitals(UUID patientId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        List<VitalsLog> logs;
        if (startDate != null && endDate != null) {
            logs = vitalsLogRepository.findByPatientIdAndDateBetweenOrderByDateDesc(patientId, startDate, endDate);
        } else {
            logs = vitalsLogRepository.findByPatientIdOrderByDateDesc(patientId);
        }

        return logs.stream().map(this::mapToResponse).toList();
    }

    private VitalsLogResponse mapToResponse(VitalsLog log) {
        return new VitalsLogResponse(
                log.getId(),
                log.getPatient().getId(),
                log.getDate(),
                log.getBloodPressureSys(),
                log.getBloodPressureDia(),
                log.getFastingSugar(),
                log.getNotes(),
                log.getCreatedAt()
        );
    }
}
