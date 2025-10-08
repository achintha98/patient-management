package com.pm.billingservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Achintha Kalunayaka
 * @since 10/7/2025
 */

@Entity
@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ScheduledJobRun {
    @Id
    private String jobName;

    private LocalDateTime lastRun;


}
