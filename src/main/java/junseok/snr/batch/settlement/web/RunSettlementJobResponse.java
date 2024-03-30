package junseok.snr.batch.settlement.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RunSettlementJobResponse {
    private String code;
    private String message;
}
