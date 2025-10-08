package com.pm.patientservice.service;

import com.pm.patientservice.dto.PagedPatientResponseDTO;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Partner;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @author Achintha Kalunayaka
 * @since 4/8/2025
 */

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private BillingServiceGrpcClient billingServiceGrpcClient;
    private KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatientList() {
        List<Partner> partners = patientRepository.findAll();
        return partners.stream().map(PatientMapper::mapToPatientResponseDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists" + patientRequestDTO.getEmail());
        }

        Partner partner = patientRepository.save(PatientMapper.mapFromPatientRequestDTO(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(partner.getId().toString(), partner.getName(), partner.getEmail());

        kafkaProducer.sendEvent(partner);

        return PatientMapper.mapToPatientResponseDTO(partner);
    }

    public PatientResponseDTO updatePatient(UUID patientId, PatientRequestDTO patientRequestDTO) {
        Partner partner = patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException
                ("Patient not found with the given ID" + patientId));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), patientId)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists" + patientRequestDTO.getEmail());
        }
        partner.setName(patientRequestDTO.getName());
        partner.setAddress(patientRequestDTO.getAddress());
        partner.setEmail(patientRequestDTO.getEmail());
        partner.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patientRepository.save(partner);
        return PatientMapper.mapToPatientResponseDTO(partner);
    }

    public void deletePatient(UUID patientId) {
        Partner partner = patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException
                ("Patient not found with the given ID" + patientId));
        patientRepository.deleteById(patientId);
    }

    public PagedPatientResponseDTO getPatients(int page, int size, String sortBy, String sortDir, String nameFilter) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Partner> patientPage;
        if (nameFilter != null && !nameFilter.isBlank()) {
            patientPage = patientRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        } else {
            patientPage = patientRepository.findAll(pageable);
        }

        List<PatientResponseDTO> patientDTOs = patientPage.getContent()
                .stream()
                .map(PatientMapper::mapToPatientResponseDTO)
                .toList();

        return new PagedPatientResponseDTO(
                patientDTOs,
                patientPage.getNumber(),
                patientPage.getTotalPages(),
                patientPage.getTotalElements(),
                patientPage.getSize(),
                patientPage.isLast()
        );
    }

}
