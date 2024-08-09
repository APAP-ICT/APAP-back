package com.example.apapbackend.Info;

import com.example.apapbackend.Info.dto.InfoRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity postInfo(@Valid @RequestBody InfoRequest infoRequest) {
        // 조건에 따라 메시지 전송
        infoService.processInfo(infoRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 객체 탐지 정보 조회
     */
    @GetMapping("{infoId}")
    public ResponseEntity<Info> getInfo(@PathVariable("infoId") Long infoId) {
        Info info = infoService.getInfo(infoId);
        return ResponseEntity.ok(info);
    }

    /**
     * 모든 객체 탐지 정보 조회
     * 시작 & 끝 기간, 카메라, 이상상황을 조건으로 받음 - null 인 경우엔 조건을 적용하지 않음
     */
    @GetMapping
    public ResponseEntity<List<Info>> getInfos(
        @RequestParam(required = false) LocalDateTime startDate,
        @RequestParam(required = false) LocalDateTime endDate,
        @RequestParam(required = false) String cameraName,
        @RequestParam(required = false) String label
    ) {
        List<Info> infos = infoService.getInfos(startDate, endDate, cameraName, label);
        return ResponseEntity.ok(infos);
    }

}
