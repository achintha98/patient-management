package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Partner;

import java.time.LocalDate;

/**
 * @author Achintha Kalunayaka
 * @since 4/8/2025
 */
public class PatientMapper {
    public static PatientResponseDTO mapToPatientResponseDTO(Partner partner) {
        return PatientResponseDTO.builder().
                id(partner.getId().toString()).name(partner.getName()).
                address(partner.getAddress()).email(partner.getEmail()).
                dateOfBirth(partner.getDateOfBirth().toString()).build();
    }

    public static Partner mapFromPatientRequestDTO(PatientRequestDTO patientRequestDTO) {
        return Partner.builder().
                name(patientRequestDTO.getName()).address(patientRequestDTO.getAddress()).email(patientRequestDTO.getEmail()).
                dateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth())).
                registeredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate())).build();
    }
}
