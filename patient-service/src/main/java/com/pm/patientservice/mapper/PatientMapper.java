package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

/**
 * @author Achintha Kalunayaka
 * @since 4/8/2025
 */
public class PatientMapper {
    public PatientResponseDTO mapToPatientDTO(Patient patient) {
        return PatientResponseDTO.builder().
                id(patient.getId().toString()).name(patient.getName()).
                address(patient.getAddress()).email(patient.getEmail()).
                dateOfBirth(patient.getDateOfBirth().toString()).build();
    }
}
