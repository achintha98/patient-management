package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Achintha Kalunayaka
 * @since 4/8/2025
 */

@Service
public class PatientService {

    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatientList() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientMapper::mapToPatientDTO).toList();
    }
}
