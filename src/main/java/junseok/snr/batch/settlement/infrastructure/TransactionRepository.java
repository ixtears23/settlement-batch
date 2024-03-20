package junseok.snr.batch.settlement.infrastructure;

import junseok.snr.batch.settlement.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStatus(String status);
}
