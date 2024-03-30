package junseok.snr.batch.dummydata.application;

import jakarta.persistence.EntityManagerFactory;
import junseok.snr.batch.settlement.domain.Transaction;
import junseok.snr.batch.settlement.infrastructure.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DummyDataBatchConfiguration {
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final TransactionRepository transactionRepository;

    @Bean
    public Job dataGeneratorJob() {
        return new JobBuilder("dataGeneratorJob", jobRepository)
                .start(dataGeneratorStep())
                .build();
    }

    @Bean
    public Step dataGeneratorStep() {
        return new StepBuilder("dataGeneratorStep", jobRepository)
                .<Transaction, Transaction>chunk(1_000, platformTransactionManager)
                .reader(dummyDataItemReader())
                .writer(transactionItemWriter())
                .taskExecutor(dummyDataTaskExecutor())
                .build();
    }

    @Bean
    public ItemWriter<Transaction> transactionItemWriter() {
        return new TransactionItemWriter(transactionRepository);
    }

    @Bean
    public ItemReader<Transaction> dummyDataItemReader() {
        return new ItemReader<>() {
            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public Transaction read() {
                // 총 생성할 레코드 수
                final int totalRecords = 1_000_000;
                if (counter.get() < totalRecords) {
                    int id = counter.incrementAndGet();
                    Random random = new Random();
                    Transaction transaction = new Transaction();
                    transaction.setTransactionDate(LocalDate.now().minusDays(random.nextInt(365)));

                    transaction.setAmount(BigDecimal.valueOf(random.nextDouble() * 10_000).setScale(2, RoundingMode.HALF_UP));
                    transaction.setDescription("Transaction " + id);
                    transaction.setStatus("미처리");
                    return transaction;
                } else {
                    return null; // 데이터 생성이 완료되면 null 반환
                }
            }
        };
    }

    @Bean
    public ItemWriter<Transaction> dummyDataItemWriter() {
        return new JpaItemWriterBuilder<Transaction>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public TaskExecutor dummyDataTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 초기 스레드 풀 사이즈
        executor.setMaxPoolSize(20); // 최대 스레드 풀 사이즈
        executor.setQueueCapacity(100); // 작업 대기열 크기
        executor.initialize();
        return executor;
    }

}
