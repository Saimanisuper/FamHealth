package com.famhealth.vault.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSmsAlert(String patientName, String medicineName, String dosage, LocalTime reminderTime) {
        String formattedTime = reminderTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        String message = String.format(
                "MEDICATION REMINDER: Dear %s, it's %s. Time to take your scheduled dose of %s (%s).",
                patientName, formattedTime, medicineName, dosage
        );
        // Simulation of SMS Gateway (e.g. AWS SNS, Twilio)
        log.info("========== [SMS GATEWAY DISPATCH SIMULATION] ==========");
        log.info("Recipient: Patient [{}]", patientName);
        log.info("Message Payload: {}", message);
        log.info("Status: DELIVERED (Simulated)");
        log.info("=======================================================");
    }

    public void sendEmailAlert(String recipientEmail, String subject, String content) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipientEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(content);
            mailSender.send(mailMessage);
            log.info("Notification email dispatched successfully to: {}", recipientEmail);
        } catch (Exception ex) {
            log.warn("Failed to dispatch email to {}: {}. Proceeding without hard failure.", recipientEmail, ex.getMessage());
        }
    }
}
