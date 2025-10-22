package com.pm.billingservice.config;

import com.pm.billingservice.model.Invoice;
import com.pm.billingservice.model.Partner;
import com.pm.billingservice.model.PartnerCommission;
import com.pm.billingservice.repository.InvoiceRepository;
import com.pm.billingservice.repository.PartnerCommissionRepository;
import com.pm.billingservice.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilder;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Achintha Kalunayaka
 * @since 10/20/2025
 */

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@EnableKafka
@RequiredArgsConstructor
@Profile({"worker1", "worker2"})
public class WorkerBatchConfig {

    @Autowired
    private final JobRepository jobRepository;


    @Autowired
    private final PlatformTransactionManager transactionManager;

    private static final Logger logger = LoggerFactory.getLogger(WorkerBatchConfig.class);


    @Bean
    public Step generateInvoiceStep(JobExplorer jobExplorer,
                                    BeanFactory beanFactory,
                                    ItemReader<Partner> reader,
                                    ItemProcessor<Partner, Invoice> processor,
                                    ItemWriter<Invoice> writer) {
        return new RemotePartitioningWorkerStepBuilder("generateInvoiceStep", jobRepository)
                .jobExplorer(jobExplorer)
                .inputChannel(requests())
                .outputChannel(replies())
                .beanFactory(beanFactory)
                .<Partner, Invoice>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(new StepExecutionListener() {
                    private long stepStart;

                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepStart = System.currentTimeMillis();
                        logger.info("Worker Step {} started", stepExecution.getStepName());
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        long duration = System.currentTimeMillis() - stepStart;
                        logger.info("Worker Step {} finished in {} ms", stepExecution.getStepName(), duration);
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    @Bean
    public MessageChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundKafkaFlow(KafkaTemplate<String, Object> kafkaTemplate) {
        return IntegrationFlow.from("requests")
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate)
                        .topic("worker-requests"))
                .get();
    }

    @Bean
    public IntegrationFlow inboundKafkaFlow(ConsumerFactory<String, Object> cf) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(cf, "worker-replies"))
                .channel("replies")
                .get();
    }

    @Bean
    @StepScope
    public ItemReader<Partner> reader(PartnerRepository partnerRepository,
                                      @Value("#{stepExecutionContext['minId']}") Long minId,
                                      @Value("#{stepExecutionContext['maxId']}") Long maxId) {
        return new RepositoryItemReaderBuilder<Partner>()
                .name("invoiceItemReader")
                .repository(partnerRepository)
                .methodName("findByIdBetween")
                .arguments(List.of(minId, maxId))
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Partner, Invoice> processor(PartnerCommissionRepository partnerCommissionRepository,
                                                     @Value("#{stepExecutionContext['minId']}") Long minId,
                                                     @Value("#{stepExecutionContext['maxId']}") Long maxId) {
        List<PartnerCommission> commissions = partnerCommissionRepository
                .findByIdBetween(minId, maxId);

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
    @StepScope
    public ItemWriter<Invoice> writer(InvoiceRepository invoiceRepository) {
        return items -> {
            invoiceRepository.saveAll(items);
        };
    }
}
