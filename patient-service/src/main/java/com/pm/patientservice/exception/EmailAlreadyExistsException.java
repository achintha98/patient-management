package com.pm.patientservice.exception;

/**
 * @author Achintha Kalunayaka
 * @since 4/9/2025
 */
public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
