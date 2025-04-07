package com.pm.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Achintha Kalunayaka
 * @since 4/7/2025
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String name;

    @Email
    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String address;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private LocalDate registeredDate;
}
