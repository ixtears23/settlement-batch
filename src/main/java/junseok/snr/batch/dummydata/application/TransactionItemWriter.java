package junseok.snr.batch.dummydata.application;

import junseok.snr.batch.settlement.domain.Transaction;
import junseok.snr.batch.settlement.infrastructure.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class TransactionItemWriter implements ItemWriter<Transaction> {
    private final TransactionRepository transactionRepository;
    @Override
    public void write(Chunk<? extends Transaction> items) {
        transactionRepository.saveAll(items);
    }
}
