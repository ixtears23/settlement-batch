package junseok.snr.batch.settlement.web;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class RunSettlementJobResponse {
    private String code;
    private String message;
}
