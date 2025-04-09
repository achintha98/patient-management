package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * @author Achintha Kalunayaka
 * @since 4/8/2025
 */

@Data
@Builder
public class PatientRequestDTO {

    @NotNull(message = "Patient name should not be empty")
    @Size(max = 100, message = "Name cannot exceed more than 100 characters")
    private String name;
    @NotNull(message = "Email cannot be empty")
    @Email
    private String email;
    @NotNull(message = "Address cannot be empty")
    private String address;
    @NotNull(message = "DOB cannot be empty")
    private String dateOfBirth;
    @NotNull(groups = CreatePatientValidationGroup.class, message = "Registered date cannot be empty")
    private String registeredDate;
}
