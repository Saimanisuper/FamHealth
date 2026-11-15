package com.famhealth.vault.repository;

import com.famhealth.vault.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, UUID> {
    List<Medication> findByPatientId(UUID patientId);

    List<Medication> findByCurrentPillCountLessThan(int threshold);

    @Query("SELECT m FROM Medication m JOIN FETCH m.patient")
    List<Medication> findAllWithPatient();
}
