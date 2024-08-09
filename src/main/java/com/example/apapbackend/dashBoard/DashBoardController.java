package com.example.apapbackend.dashBoard;

import com.example.apapbackend.dashBoard.dto.DashBoardBottomResponse;
import com.example.apapbackend.dashBoard.dto.DashBoardTopResponse;
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
     * (대시 보드 상단) 통계 결과 조회
     * 1. 지난 주 대비, 가장 많이 발생한 이상 상황
     * 2. 지난 달 대비, 이상 상황이 가장 많이 발생한 구역(카메라)
     * 3. 지난 주 대비, 이상 상황 발생 횟수 평균
     */
    @GetMapping("/top-infos")
    public ResponseEntity<DashBoardTopResponse> getDashBoardTopInfo() {
        DashBoardTopResponse dashBoardTopInfo = dashBoardService.getTopInfo();
        return ResponseEntity.ok(dashBoardTopInfo);
    }

    /**
     * (대시 보드 하단) 통계 결과 조회
     * 1. 최근 5일간 이상 상황 통계
     * 2. 최근 5일간 이상 상황 통계
     */
    @GetMapping("/top-infos")
    public ResponseEntity<DashBoardBottomResponse> getDashBoardBottomInfo() {
        DashBoardBottomResponse dashBoardBottomInfo = dashBoardService.getBottomInfo();
        return ResponseEntity.ok(dashBoardBottomInfo);
    }
}
