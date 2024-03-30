package junseok.snr.batch.settlement.application;

import jakarta.persistence.EntityManagerFactory;
import junseok.snr.batch.settlement.domain.Settlement;
import junseok.snr.batch.settlement.domain.Transaction;
import junseok.snr.batch.settlement.domain.TransactionFee;
import junseok.snr.batch.settlement.infrastructure.SettlementRepository;
import junseok.snr.batch.settlement.infrastructure.TransactionFeeRepository;
import junseok.snr.batch.settlement.infrastructure.TransactionRepository;
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
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class SettlementBatchConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;

    private final TransactionRepository transactionRepository;
    private final TransactionFeeRepository transactionFeeRepository;
    private final SettlementRepository settlementRepository;

    @Bean
    public Job settlementJob() {
        return new JobBuilder("settlementJob", jobRepository)
                .start(settlementStep())
                .build();
    }

    @Bean
    public Step settlementStep() {
        return new StepBuilder("settlementStep", jobRepository)
                .<Transaction, TransactionFee>chunk(10_000, platformTransactionManager)
                .reader(transactionReader())
                .processor(transactionProcessor())
                .writer(settlementWriter())
                .taskExecutor(settlementTaskExecutor())
                .build();
    }

    @Bean
    public ItemReader<Transaction> transactionReader() {
        return new JpaPagingItemReaderBuilder<Transaction>()
                .name("transactionReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT t FROM Transaction t WHERE t.status = '미처리'")
                .pageSize(10_000)
                .build();
    }

    @Bean
    public ItemProcessor<Transaction, TransactionFee> transactionProcessor() {
        return transaction -> {
            BigDecimal feeAmount = transaction.getAmount().multiply(new BigDecimal("0.01"));
            TransactionFee transactionFee = new TransactionFee();
            transactionFee.setTransaction(transaction);
            transactionFee.setFeeAmount(feeAmount);
            return transactionFee;
        };
    }


    @Bean
    public ItemWriter<TransactionFee> settlementWriter() {
        return transactionFees -> {
            transactionFees.forEach(transactionFee -> {
                Transaction transaction = transactionFee.getTransaction();
                transaction.setStatus("처리됨");
                transactionRepository.save(transaction);
                transactionFeeRepository.save(transactionFee);
            });

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (TransactionFee transactionFee : transactionFees) {
                totalAmount = totalAmount.add(transactionFee.getFeeAmount());
            }

            int transactionCount = transactionFees.size();

            Settlement settlement = new Settlement();
            settlement.setSettlementDate(LocalDate.now());
            settlement.setTotalAmount(totalAmount);
            settlement.setTransactionCount(transactionCount);
            settlementRepository.save(settlement);
        };
    }

    @Bean
    public TaskExecutor settlementTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("batch-executor-");
        executor.initialize();
        return executor;
    }
}
