package junseok.snr.batch.settlement.infrastructure;

import junseok.snr.batch.settlement.domain.TransactionFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionFeeRepository extends JpaRepository<TransactionFee, Long> {
}
