package com.example.apapbackend.domain.Info;

import com.example.apapbackend.domain.Info.dto.InfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/infos")
public class InfoController {

    private final InfoService infoService;

    /**
     * 탐지 결과를 받아서 메시지 전송
     */
    @PostMapping
    @Operation(
        summary = "AI 서버로부터 이상 상황 정보 받음, 조건부 알림 전송",
        description = "이상 상황의 조건에 따라 조건부 알림을 전송합니다. - 테스트"
    )
    public ResponseEntity postInfo(@Valid @RequestBody InfoRequest infoRequest) {
        // 조건에 따라 메시지 전송
        infoService.processInfo(infoRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 객체 탐지 정보 조회
     */
    @GetMapping("{infoId}")
    @Operation(summary = "객체 탐지 정보 조회", description = "객체 탐지 정보를 조회합니다.")
    public ResponseEntity<Info> getInfo(@PathVariable("infoId") Long infoId) {
        Info info = infoService.getInfo(infoId);
        return ResponseEntity.ok(info);
    }

    /**
     * 모든 객체 탐지 정보 조회 시작 & 끝 기간, 카메라, 이상상황을 조건으로 받음 - null 인 경우엔 조건을 적용하지 않음
     */
    @GetMapping
    @Operation(
        summary = "조건별 객체 탐지 정보 목록 조회",
        description = "쿼리 파라미터 조건 별 객체 탐지 정보 목록을 조회합니다."
    )
    public ResponseEntity<List<Info>> getInfos(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(required = false) String cameraName,
        @RequestParam(required = false) String label
    ) {
        List<Info> infos = infoService.getInfos(startDate, endDate, cameraName, label);
        return ResponseEntity.ok(infos);
    }

}
