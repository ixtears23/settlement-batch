package junseok.snr.batch.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    BATCH_JOB_FAILED("배치 작업 실행 실패");
    private final String description;
}
