package com.pm.billingservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

/**
 * @author Achintha Kalunayaka
 * @since 10/7/2025
 */

@Service
@RequiredArgsConstructor
@Profile("master")
public class MonthlyCommissionJob {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyCommissionJob.class);

    @Autowired
    private ScheduledJobService scheduledJobService;

    @Autowired
    private final JobLauncher jobLauncher;

    @Autowired
    private final Job monthlyInvoiceJob;

//    @Scheduled(cron = "0 0 0 1 * *", zone = "UTC")
//@Scheduled(initialDelay = 0, fixedRate = 600000) // 600000 ms = 10 minutes
    public void generateMonthlyInvoice() {
        String jobName = "MONTHLY_invoice";
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // check if already run this month
//        if (scheduledJobService.hasRunThisMonth(jobName, now)) {
//            logger.info("Skipping job {}, already executed for month {}", jobName, now.getMonth());
//            return;
//        }

        try {
            logger.info("Running monthly invoice for {}", now.getMonth());

            // your main logic here
            JobParameters params = new JobParametersBuilder()
                    .addString("month", YearMonth.now().minusMonths(1).toString()) // previous month
                    .addLong("time", System.currentTimeMillis()) // ensures uniqueness
                    .toJobParameters();

            jobLauncher.run(monthlyInvoiceJob, params);

            // mark job as completed
            scheduledJobService.updateLastRun(jobName, now);
            logger.info("Monthly invoice completed successfully");

        } catch (Exception ex) {
            logger.error("Error during monthly invoice job: {}", ex.getMessage(), ex);
            // optionally send alert or retry logic
        }
    }

    private void runInvoiceGeneration() {

    }


}
