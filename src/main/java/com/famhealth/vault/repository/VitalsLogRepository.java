package com.famhealth.vault.repository;

import com.famhealth.vault.entity.VitalsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VitalsLogRepository extends JpaRepository<VitalsLog, UUID> {
    List<VitalsLog> findByPatientIdAndDateBetweenOrderByDateDesc(UUID patientId, LocalDateTime startDate, LocalDateTime endDate);
    List<VitalsLog> findByPatientIdOrderByDateDesc(UUID patientId);
}
