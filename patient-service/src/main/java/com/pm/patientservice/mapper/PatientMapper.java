package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

import java.time.LocalDate;

/**
 * @author Achintha Kalunayaka
 * @since 4/8/2025
 */
public class PatientMapper {
    public static PatientResponseDTO mapToPatientResponseDTO(Patient patient) {
        return PatientResponseDTO.builder().
                id(patient.getId().toString()).name(patient.getName()).
                address(patient.getAddress()).email(patient.getEmail()).
                dateOfBirth(patient.getDateOfBirth().toString()).build();
    }

    public static Patient mapFromPatientRequestDTO(PatientRequestDTO patientRequestDTO) {
        return Patient.builder().
                name(patientRequestDTO.getName()).address(patientRequestDTO.getAddress()).email(patientRequestDTO.getEmail()).
                dateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth())).
                registeredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate())).build();
    }
}
