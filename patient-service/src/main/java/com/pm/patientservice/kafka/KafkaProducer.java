package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Patient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

import static com.pm.patientservice.exception.GlobalExceptionHandler.log;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();
        try {
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PatientCreated event to Kafka: {}", patientEvent);
        }
    }
}
//$1aadda41-80d9-4b2c-8968-e392eb996309PATIENT_CREATED"Mary Jane Kafka Producer2*+mary_billing_kafka_Producer2.jane@gmail.com