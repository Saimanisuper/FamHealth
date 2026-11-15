package com.famhealth.vault.service;

import com.famhealth.vault.entity.Medication;
import com.famhealth.vault.repository.MedicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Component
public class MedicationScheduler {

    private static final Logger log = LoggerFactory.getLogger(MedicationScheduler.class);

    private final MedicationRepository medicationRepository;
    private final NotificationService notificationService;

    public MedicationScheduler(MedicationRepository medicationRepository, NotificationService notificationService) {
        this.medicationRepository = medicationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Executes every hour at minute 0: "0 0 * * * *"
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void executeHourlyMedicationCheck() {
        log.info("Starting hourly Medication inventory and reminder schedule check...");
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour();

        List<Medication> allMedications = medicationRepository.findAllWithPatient();

        for (Medication medication : allMedications) {
            String patientName = medication.getPatient() != null ? medication.getPatient().getName() : "Unknown";

            // 1. Low-inventory warning check (< 5 pills remaining)
            if (medication.getCurrentPillCount() != null && medication.getCurrentPillCount() < 5) {
                log.warn(">>> [LOW INVENTORY WARNING] Patient: '{}' | Medicine: '{}' | Remaining: {} pills (Threshold: < 5) - Please order a refill promptly!",
                        patientName,
                        medication.getMedicineName(),
                        medication.getCurrentPillCount()
                );
            }

            // 2. Reminder time check (hourly match simulation)
            if (medication.getReminderTimes() != null) {
                for (LocalTime reminderTime : medication.getReminderTimes()) {
                    if (reminderTime.getHour() == currentHour) {
                        notificationService.sendSmsAlert(
                                patientName,
                                medication.getMedicineName(),
                                medication.getDosage(),
                                reminderTime
                        );
                    }
                }
            }
        }
        log.info("Hourly medication job check completed for {} medication records.", allMedications.size());
    }
}
