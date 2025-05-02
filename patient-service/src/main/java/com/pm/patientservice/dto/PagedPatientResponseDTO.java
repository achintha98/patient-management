package com.pm.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Achintha Kalunayaka
 * @since 5/2/2025
 */

@Data
@AllArgsConstructor
public class PagedPatientResponseDTO {

    private List<PatientResponseDTO> patients;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;
    private boolean isLast;
}
