package com.famhealth.vault.config;

import com.famhealth.vault.entity.*;
import com.famhealth.vault.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDefaultData(
            UserRepository userRepository,
            PatientRepository patientRepository,
            VitalsLogRepository vitalsLogRepository,
            MedicationRepository medicationRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("No users found in database. Initializing default bootstrap users and demo health records...");

                User admin = new User("admin", passwordEncoder.encode("Admin@12345"), Role.ADMIN);
                User viewer = new User("viewer", passwordEncoder.encode("Viewer@12345"), Role.VIEWER);
                User nurse = new User("nurse", passwordEncoder.encode("Nurse@12345"), Role.NURSE);

                userRepository.save(admin);
                userRepository.save(viewer);
                userRepository.save(nurse);

                // Sample patient for admin
                Patient patient = new Patient("Robert Vance (Father)", 74, "A+", "Aspirin, Penicillin", admin);
                patientRepository.save(patient);

                // Seed sample vitals logs
                vitalsLogRepository.save(new VitalsLog(patient, LocalDateTime.now().minusDays(3), 130, 85, 105.0, "Morning reading before breakfast"));
                vitalsLogRepository.save(new VitalsLog(patient, LocalDateTime.now().minusDays(2), 128, 82, 102.5, "Slight headache reported"));
                vitalsLogRepository.save(new VitalsLog(patient, LocalDateTime.now().minusDays(1), 122, 80, 98.0, "Normal reading after evening walk"));
                vitalsLogRepository.save(new VitalsLog(patient, LocalDateTime.now(), 120, 78, 96.0, "Optimal vitals today"));

                // Seed sample medications (one normal, one low-stock warning < 5)
                medicationRepository.save(new Medication(patient, "Amlodipine (Blood Pressure)", "5mg daily", 3, 22, List.of(LocalTime.of(8, 0))));
                medicationRepository.save(new Medication(patient, "Metformin (Blood Sugar)", "500mg twice daily", 1, 3, List.of(LocalTime.of(8, 30), LocalTime.of(20, 30))));

                log.info("Default bootstrap users and sample patient seeded successfully:");
                log.info(" -> Admin:  username='admin'  | password='Admin@12345'  | role=ROLE_ADMIN");
                log.info(" -> Viewer: username='viewer' | password='Viewer@12345' | role=ROLE_VIEWER");
                log.info(" -> Nurse:  username='nurse'  | password='Nurse@12345'  | role=ROLE_NURSE");
                log.info(" -> Demo Patient: 'Robert Vance (Father)', Age: 74, Blood Group: A+");
            }
        };
    }
}
