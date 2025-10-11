package com.pm.billingservice.config;

import com.pm.billingservice.model.Invoice;
import com.pm.billingservice.model.Partner;
import com.pm.billingservice.model.PartnerCommission;
import com.pm.billingservice.repository.InvoiceRepository;
import com.pm.billingservice.repository.PartnerCommissionRepository;
import com.pm.billingservice.repository.PartnerRepository;
import com.pm.billingservice.service.MonthlyCommissionJob;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Achintha Kalunayaka
 * @since 10/9/2025
 */

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class InvoiceBatchConfig {

    @Autowired
    private final JobRepository jobRepository;

    @Autowired
    private final PlatformTransactionManager transactionManager;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceBatchConfig.class);


    @Bean
    public Job monthlyInvoiceJob(Step generateInvoiceStep) {
        return new JobBuilder("monthlyInvoiceJob", jobRepository)
                .listener(new JobExecutionListener() {
                    private long startTime;

                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        startTime = System.currentTimeMillis();
                        logger.info("Monthly Invoice Job started at {}", LocalDateTime.now());
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        long duration = System.currentTimeMillis() - startTime;
                        logger.info("Monthly Invoice Job finished at {}, total time = {} ms", LocalDateTime.now(), duration);
                    }
                })

                .start(generateInvoiceStep)
                .build();
    }

    @Bean
    public Step generateInvoiceStep(ItemReader<Partner> reader,
                                    ItemProcessor<Partner, Invoice> processor,
                                    ItemWriter<Invoice> writer) {
        return new StepBuilder("generateInvoiceStep", jobRepository)
                .<Partner, Invoice>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(new StepExecutionListener() {
                    private long stepStart;

                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepStart = System.currentTimeMillis();
                        logger.info("Step {} started", stepExecution.getStepName());
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        long duration = System.currentTimeMillis() - stepStart;
                        logger.info("Step {} finished in {} ms", stepExecution.getStepName(), duration);
                        return stepExecution.getExitStatus();
                    }
                })

                .build();
    }

    @Bean
    public ItemReader<Partner> reader(PartnerRepository partnerRepository) {
        return new RepositoryItemReaderBuilder<Partner>()
                .name("invoiceItemReader")
                .repository(partnerRepository)
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Partner, Invoice> processor(PartnerCommissionRepository partnerCommissionRepository) {
        List<PartnerCommission> commissions =
                partnerCommissionRepository.findAll();

        Map<Long, BigDecimal> totalByPartner = commissions.stream()
                .collect(Collectors.groupingBy(
                        commission -> commission.getPartnerId(),
                        Collectors.mapping(
                                PartnerCommission::getCommissionAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        return partner -> {

            BigDecimal total = totalByPartner.getOrDefault(partner.getId(), BigDecimal.ZERO);

            Invoice invoice = new Invoice();
            invoice.setPartner(partner);
            invoice.setCommission(total);
            invoice.setCreatedAt(LocalDateTime.now());
            return invoice;
        };
    }

    @Bean
    public ItemWriter<Invoice> writer(InvoiceRepository invoiceRepository) {
        return items -> {
            invoiceRepository.saveAll(items);
        };
    }






}
