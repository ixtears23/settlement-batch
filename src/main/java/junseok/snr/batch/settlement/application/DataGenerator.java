package junseok.snr.batch.settlement.application;

import junseok.snr.batch.settlement.domain.Transaction;
import junseok.snr.batch.settlement.infrastructure.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class DataGenerator {
    private final TransactionRepository transactionRepository;

    @Async("dummyDataTaskExecutor")
    public void generateTransactions(int start, int end) {
        Random random = new Random();
        for (int i = start; i <= end; i++) {
            Transaction transaction = new Transaction();
            transaction.setTransactionDate(LocalDate.now().minusDays(random.nextInt(365)));
            transaction.setAmount(BigDecimal.valueOf(random.nextDouble() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP));
            transaction.setDescription("Transaction " + i);
            transaction.setStatus("미처리");
            transactionRepository.save(transaction);
        }
    }

    @Bean
    public CommandLineRunner generateTestData() {
        return args -> {
            int totalRecords = 1_000_000;
            int batchSize = 250_000; // 분할할 배치 크기
            for (int i = 1; i <= totalRecords; i += batchSize) {
                generateTransactions(i, Math.min(i + batchSize - 1, totalRecords));
            }
        };
    }
}
