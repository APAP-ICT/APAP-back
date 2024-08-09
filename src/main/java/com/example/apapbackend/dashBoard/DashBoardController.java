package com.example.apapbackend.dashBoard;

import com.example.apapbackend.dashBoard.dto.DashBoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashBoardController {

    private final DashBoardService dashBoardService;

    /**
     * 대시 보드 통계 결과 조회
     * 1. 지난 주 대비, 가장 많이 발생한 이상 상황
     * 2. 지난 달 대비, 가장 많이 발생한 구역(카메라)
     * 3. 지난 주 대비, 이상 상황 발생 횟수 평균
     */
    @GetMapping()
    public ResponseEntity<DashBoardResponse> getDashBoardInfo() {
        DashBoardResponse dashBoardInfo = dashBoardService.getInfo();
        return ResponseEntity.ok(dashBoardInfo);
    }
}
