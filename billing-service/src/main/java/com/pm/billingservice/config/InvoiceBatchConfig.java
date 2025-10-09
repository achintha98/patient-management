package com.pm.billingservice.config;

import com.pm.billingservice.model.Invoice;
import com.pm.billingservice.model.Partner;
import com.pm.billingservice.repository.InvoiceRepository;
import com.pm.billingservice.repository.PartnerCommissionRepository;
import com.pm.billingservice.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import java.util.Map;

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

    @Bean
    public Job monthlyInvoiceJob(Step generateInvoiceStep) {
        return new JobBuilder("monthlyInvoiceJob", jobRepository)
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
                .build();
    }

    @Bean
    public ItemReader<Partner> reader(PartnerRepository partnerRepository) {
        return new RepositoryItemReaderBuilder<Partner>()
                .repository(partnerRepository)
                .methodName("findAllActivePartners")
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Partner, Invoice> processor(PartnerCommissionRepository partnerCommissionRepository) {
        return partner -> {
            BigDecimal totalCommission = partnerCommissionRepository.findById(partner.getId()).
                    stream().map(partnerCommission ->
                        partnerCommission.getCommissionAmount()
                    ).reduce(BigDecimal.ZERO, (subTotal, element) -> subTotal.add(element));
            // calculate invoice
            Invoice invoice = new Invoice();
            invoice.setPartner(partner);
            invoice.setCommission(totalCommission);
            invoice.setCreatedAt(LocalDateTime.now());
            return invoice;
        };
    }

    @Bean
    public ItemWriter<Invoice> writer(InvoiceRepository invoiceRepository) {
        return items -> invoiceRepository.saveAll(items);
    }






}
