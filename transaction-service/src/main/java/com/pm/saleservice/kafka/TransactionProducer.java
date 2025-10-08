package com.pm.saleservice.kafka;

import com.pm.saleservice.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import transaction.events.TransactionEvent;

/**
 * @author Achintha Kalunayaka
 * @since 9/15/2025
 */

@Service
public class TransactionProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TransactionProducer.class);

    public TransactionProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Transaction transaction) {
        TransactionEvent patientEvent = TransactionEvent.newBuilder().setTransactionId(transaction.getTransactionId().toString()).
                setPartnerId(transaction.getPartnerId().toString()).setAmount(transaction.getAmount().toString()).
                setCurrency(transaction.getCurrency()).build();

        try {
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception exception) {
            logger.error("Error Sending patient created event: {}", patientEvent);
        }

    }

}
