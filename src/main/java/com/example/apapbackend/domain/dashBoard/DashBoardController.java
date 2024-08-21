package com.example.apapbackend.domain.dashBoard;

import com.example.apapbackend.domain.dashBoard.dto.DashBoardBottomResponse;
import com.example.apapbackend.domain.dashBoard.dto.DashBoardTopResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping("/top-infos")
    @Operation(
        summary = "대시 보드 상단 통계 결과 조회",
        description = "1. 지난 주 대비, 가장 많이 발생한 이상 상황\n"
            + "2. 지난 달 대비, 이상 상황이 가장 많이 발생한 구역(카메라)\n"
            + "3. 지난 주 대비, 이상 상황 발생 횟수 일 평균"
    )
    public ResponseEntity<DashBoardTopResponse> getDashBoardTopInfo() {
        DashBoardTopResponse dashBoardTopInfo = dashBoardService.getTopInfo();
        return ResponseEntity.ok(dashBoardTopInfo);
    }

    @GetMapping("/bottom-infos")
    @Operation(
        summary = "대시 보드 하단 통계 결과 조회",
        description = "1. 최근 3일간 일별 가장 많이 발생한 이상 상황 종류 4개 및 횟수\n"
            + "2. 최근 3일간 가장 많이 발생한 이상 상황 종류 3개 및 횟수"
    )
    public ResponseEntity<DashBoardBottomResponse> getDashBoardBottomInfo() {
        DashBoardBottomResponse dashBoardBottomInfo = dashBoardService.getBottomInfo();
        return ResponseEntity.ok(dashBoardBottomInfo);
    }
}
