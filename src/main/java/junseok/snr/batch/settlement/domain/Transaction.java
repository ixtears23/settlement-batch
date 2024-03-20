package junseok.snr.batch.settlement.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private String description;
    private String status;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TransactionFee> transactionFees = new HashSet<>();

    public void setTransactionFees(Set<TransactionFee> transactionFees) {
        this.transactionFees = transactionFees;
        for (TransactionFee fee : transactionFees) {
            fee.setTransaction(this);
        }
    }

    public void addTransactionFee(TransactionFee fee) {
        this.transactionFees.add(fee);
        fee.setTransaction(this);
    }
}
