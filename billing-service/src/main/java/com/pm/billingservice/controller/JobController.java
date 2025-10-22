package com.pm.billingservice.controller;

import com.pm.billingservice.service.MonthlyCommissionJob;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Achintha Kalunayaka
 * @since 10/22/2025
 */

@RestController
@RequiredArgsConstructor
@Profile("master")
public class JobController {

    private final MonthlyCommissionJob monthlyInvoiceJob;


    @PostMapping("/start-invoice-job")
    public String startJob() throws Exception {
        monthlyInvoiceJob.generateMonthlyInvoice();
        return "Job started";
    }

}
