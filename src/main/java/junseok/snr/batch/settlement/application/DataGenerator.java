package junseok.snr.batch.settlement.application;

import junseok.snr.batch.settlement.domain.Transaction;
import junseok.snr.batch.settlement.infrastructure.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Component
public class DataGenerator {

    @Bean
    public CommandLineRunner generateTestData(TransactionRepository transactionRepository) {
        return args -> {
            Random random = new Random();
            for (int i = 1; i <= 1000000; i++) {
                Transaction transaction = new Transaction();
                transaction.setTransactionDate(LocalDate.now().minusDays(random.nextInt(365)));
                transaction.setAmount(BigDecimal.valueOf(random.nextDouble() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP));
                transaction.setDescription("Transaction " + i);
                transaction.setStatus("미처리");
                transactionRepository.save(transaction);

                if (i % 10000 == 0) {
                    System.out.println(i + " transactions generated.");
                }
            }
        };
    }
}