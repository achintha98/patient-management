package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Partner;
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

    public void sendEvent(Partner partner) {
        PatientEvent patientEvent = PatientEvent.newBuilder().setPatientId(partner.getId().toString()).
                setName(partner.getName()).setEmail(partner.getEmail()).setEventType("PATIENT_CREATED").build();

        try {
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception exception) {
            logger.error("Error Sending patient created event: {}", patientEvent);
        }

    }
}
