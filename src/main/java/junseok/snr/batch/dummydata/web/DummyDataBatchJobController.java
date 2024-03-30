package junseok.snr.batch.dummydata.web;

import junseok.snr.batch.common.dto.ErrorCode;
import junseok.snr.batch.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DummyDataBatchJobController {
    private final JobLauncher jobLauncher;
    private final Job dataGeneratorJob;

    @PostMapping("/run-data-generator-job")
    public ResponseEntity<CommonResponse> runDataGeneratorJob(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        try {
            final LocalDateTime time = dateTime != null ? dateTime : LocalDateTime.now();
            final JobParameters jobParameters = new JobParametersBuilder()
                    .addString("time", time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                    .toJobParameters();

            final JobExecution jobExecution = jobLauncher.run(dataGeneratorJob, jobParameters);

            return ResponseEntity.ok(CommonResponse.builder()
                    .code("SUCCESS")
                    .message(jobExecution.getStatus().toString())
                    .build());
        } catch (Exception e) {
            log.error("=== runSettlementJob Error", e);
            return ResponseEntity.internalServerError().body(CommonResponse.builder()
                    .code(ErrorCode.BATCH_JOB_FAILED.name())
                    .message(ErrorCode.BATCH_JOB_FAILED.getDescription())
                    .build());
        }
    }
}
