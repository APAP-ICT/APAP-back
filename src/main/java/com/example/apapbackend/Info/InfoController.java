package com.example.apapbackend.Info;

import com.example.apapbackend.S3.S3ImageFileUploader;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InfoController {

    private final S3ImageFileUploader s3ImageFileUploader;
    private final InfoService infoService;

    /**
     * 객체 탐지 정보 저장
     *
     * @param localDateTime
     * @param label
     * @param base64Image
     * @return
     */
    @PostMapping("/infos")
    public ResponseEntity<Info> postInfo(
        @RequestParam("datetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime,
        @RequestParam("label") String label,
        @RequestParam("image") String base64Image
    ) {
        log.info("/file POST 요청");
        ResponseEntity<Info> response = infoService.save(localDateTime, label, base64Image);
        return response;
    }

    /**
     * 모든 객체 탐지 정보 조회
     *
     * @return
     */
    @GetMapping("/infos")
    public ResponseEntity<List<Info>> getInfos() {
        log.info("/infos GET 요청");
        ResponseEntity<List<Info>> response = infoService.getInfos();
        return response;
    }
}
