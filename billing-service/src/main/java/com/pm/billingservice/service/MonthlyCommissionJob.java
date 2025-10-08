package com.pm.billingservice.service;

import com.pm.billingservice.repository.ScheduledJobRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Achintha Kalunayaka
 * @since 10/7/2025
 */

@Service
public class MonthlyCommissionJob {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyCommissionJob.class);

    @Autowired
    private ScheduledJobRepository scheduledJobRepository;

    @Scheduled(cron = "0 0 0 1 * *", zone = "UTC")
    @Transactional
    public void generateMonthlyReport() {
        String jobName = "MONTHLY_REPORT";
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // check if already run this month
        if (scheduledJobRepository.hasRunThisMonth(jobName, now)) {
            logger.info("Skipping job {}, already executed for month {}", jobName, now.getMonth());
            return;
        }

        try {
            logger.info("Running monthly report for {}", now.getMonth());

            // your main logic here
            runReportGeneration();

            // mark job as completed
            scheduledJobRepository.updateLastRun(jobName, now);
            logger.info("Monthly report completed successfully");

        } catch (Exception ex) {
            logger.error("Error during monthly report job: {}", ex.getMessage(), ex);
            // optionally send alert or retry logic
        }
    }

    private void runReportGeneration() {
        // your real business logic
    }


}
