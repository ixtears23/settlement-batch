package junseok.snr.batch.settlement.infrastructure;

import junseok.snr.batch.settlement.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
