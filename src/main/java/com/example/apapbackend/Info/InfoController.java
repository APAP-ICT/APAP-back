package com.example.apapbackend.Info;

import com.example.apapbackend.Info.dto.InfoRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InfoController {

    private final InfoService infoService;

    /**
     * 탐지 결과를 받아서 메시지 전송
     */
    @PostMapping("/api/infos")
    public ResponseEntity postInfo(@Valid @RequestBody InfoRequest infoRequest) {
        // 조건에 따라 메시지 전송
        infoService.processInfo(infoRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 모든 객체 탐지 정보 조회
     */
    @GetMapping("/api/infos")
    public ResponseEntity<List<Info>> getInfos() {
        log.info("/infos GET 요청");
        ResponseEntity<List<Info>> response = infoService.getInfos();
        return response;
    }
}
