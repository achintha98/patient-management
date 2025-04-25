package com.pm.patientservice.kafka;

import com.pm.patientservice.exception.GlobalExceptionHandler;
import com.pm.patientservice.model.Patient;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

/**
 * @author Achintha Kalunayaka
 * @since 4/19/2025
 */

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent patientEvent = PatientEvent.newBuilder().setPatientId(patient.getId().toString()).
                setName(patient.getName()).setEmail(patient.getEmail()).setEventType("PATIENT_CREATED").build();

        try {
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception exception) {
            logger.error("Error Sending patient created event: {}", patientEvent);
        }

    }
}
