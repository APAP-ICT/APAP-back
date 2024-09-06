package com.example.apapbackend.domain.report;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ResourceLoader resourceLoader;

    @Value("classpath:static/report.pdf")
    private Resource resource;

    @GetMapping
    public ResponseEntity<Resource> getDailyReport() {
        // 리소스 로더를 사용하여 리소스를 로드합니다.
        org.springframework.core.io.Resource resource = resourceLoader.getResource(
            "classpath:static/report.pdf");

        try {
            // 파일 다운로드를 위해 적절한 헤더를 설정합니다.
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"2024_08_30_daily_report.pdf\"")
                .body(this.resource);
        } catch (Exception e) {
            // 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Operation(summary = "주간 보고서 다운로드")
    public void getWeeklyOfDay(

    ) {

    }
}
