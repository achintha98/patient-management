package com.pm.billingservice.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

/**
 * @author Achintha Kalunayaka
 * @since 10/9/2025
 */

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableBatchIntegration
@EnableKafka
@Profile("master")
public class MasterBatchConfig {

    @Autowired
    private final JobRepository jobRepository;

    @Autowired
    private final PartnerRangePartitioner partitioner;

    @Autowired
    private final PlatformTransactionManager transactionManager;

    private static final Logger logger = LoggerFactory.getLogger(MasterBatchConfig.class);


    @Bean
    public Job monthlyInvoiceJob(Step masterStep) {
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

                .start(masterStep)
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
                        .topic("partition-requests"))
                .get();
    }

    @Bean
    public IntegrationFlow inboundKafkaFlow(ConsumerFactory<String, Object> cf) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(cf, "partition-replies"))
                .channel("replies")
                .get();
    }

    @Bean
    public Step masterStep(MessageChannel requests,
                           MessageChannel replies,
                           BeanFactory beanFactory,
                           JobExplorer jobExplorer) {
        return new RemotePartitioningManagerStepBuilder("master-step", jobRepository)
                .partitioner("workerStep", partitioner)
                .gridSize(2) // 8 parallel partitions
                .inputChannel(replies)
                .outputChannel(requests)
                .beanFactory(beanFactory)
                .jobExplorer(jobExplorer)
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








}
