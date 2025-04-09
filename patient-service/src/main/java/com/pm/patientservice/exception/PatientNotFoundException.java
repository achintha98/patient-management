package com.pm.patientservice.exception;

/**
 * @author Achintha Kalunayaka
 * @since 4/9/2025
 */
public class PatientNotFoundException extends RuntimeException{

    public PatientNotFoundException(String message) {
        super(message);
    }
}
