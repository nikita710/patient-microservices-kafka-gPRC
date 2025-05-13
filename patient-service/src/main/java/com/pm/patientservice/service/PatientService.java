package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.exception.EmailAlreadyExists;
import com.pm.patientservice.exception.PatientNotFound;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDto> getAllPatients() {
        List<Patient> patientList = patientRepository.findAll();

        return patientList.stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if (patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExists("Email already exists");
        }
        Patient savedPatient = patientRepository.save(PatientMapper.toEntity(patientRequestDto));

        billingServiceGrpcClient.createBillingAccount(
                savedPatient.getId().toString(),
                savedPatient.getName(),
                savedPatient.getEmail()
        );

        kafkaProducer.sendEvent(savedPatient);
        return PatientMapper.toDto(savedPatient);
    }

    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {
        if (patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)) {
            throw new EmailAlreadyExists("Email already exists...");
        }
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFound("Patient not found with id: " + id));
        existingPatient.setName(patientRequestDto.getName());
        existingPatient.setEmail(patientRequestDto.getEmail());
        existingPatient.setAddress(patientRequestDto.getAddress());
        existingPatient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        return PatientMapper.toDto(patientRepository.save(existingPatient));
    }

    public void deletePatient(UUID id) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFound("Patient not found with id: " + id));
        patientRepository.delete(existingPatient);
    }
}
