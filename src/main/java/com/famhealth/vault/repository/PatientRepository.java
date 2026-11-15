package com.famhealth.vault.repository;

import com.famhealth.vault.entity.Patient;
import com.famhealth.vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    List<Patient> findByUser(User user);
    List<Patient> findByUserId(UUID userId);
    Optional<Patient> findByIdAndUserId(UUID id, UUID userId);
}
