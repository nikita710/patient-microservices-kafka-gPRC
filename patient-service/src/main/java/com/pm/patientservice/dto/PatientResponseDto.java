package com.pm.patientservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PatientResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
}
