package com.example.apapbackend.domain.Info;

import com.example.apapbackend.domain.Info.dto.InfoRequest;
import com.example.apapbackend.domain.fcm.FCMService;
import com.example.apapbackend.domain.fcm.FCMToken;
import com.example.apapbackend.domain.fcm.FCMTokenRepository;
import com.example.apapbackend.global.gemini.GeminiService;
import com.example.apapbackend.global.s3.S3ImageFileUploader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoService {

    private final S3ImageFileUploader s3ImageFileUploader;
    private final InfoRepository infoRepository;
    private final InfoTracker infoTracker;
    private final FCMService fcmService;
    private final FCMTokenRepository fcmTokenRepository;
    private final GeminiService geminiService;

    /**
     * 객체 정보 저장
     */
    public Info save(String cameraName, LocalDateTime localDateTime, String label,
        String base64Image) {
        try {
            String objUrl = s3ImageFileUploader.uploadImageFromUrlToS3(base64Image);

            String description = geminiService.getDescription(objUrl);

            Info info = new Info(cameraName, localDateTime, label, objUrl, description);
            return infoRepository.save(info);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이상 상황 정보 저장 중 에러 발생");
        }
    }

    /**
     * 모든 객체 탐지 정보 조회 시작 & 끝 기간, 카메라, 이상상황을 조건으로 받음 - null 인 경우엔 조건을 적용하지 않음
     */
    public List<Info> getInfos(LocalDateTime startDate, LocalDateTime endDate, String cameraName,
        String label) {
        Specification<Info> spec = Specification.where(InfoSpecifications.hasStartDate(startDate))
            .and(InfoSpecifications.hasEndDate(endDate))
            .and(InfoSpecifications.hasCameraName(cameraName))
            .and(InfoSpecifications.hasLabel(label));

        return infoRepository.findAll(spec);
    }

    /**
     * 조건에 따라 메시지 전송
     */
    @Transactional
    public void processInfo(InfoRequest infoRequest) {
        List<FCMToken> fcmTokens = fcmTokenRepository.findAll();
        List<String> tokens = fcmTokens.stream()
            .map(FCMToken::getToken)
            .toList();

        // 현재 라벨과 타임스탬프
        String label = infoRequest.label();
        LocalDateTime currentTimestamp = infoRequest.localDateTime();
        // 현재 라벨에 대한 직전 타임스탬프
        LocalDateTime lastTimestamp = infoTracker.getTimestamp(label);

        // 알림을 보낼 조건 확인
        if (lastTimestamp != null) {
            Duration duration = Duration.between(lastTimestamp, currentTimestamp);
            // 같은 라벨이 30초 이상 지났다면 알림 전송 - "지속"
            if (duration.getSeconds() >= 30) {
                log.info("same label passed for 30 sec more");
                Info savedInfo = save(infoRequest.cameraName(), currentTimestamp, label,
                    infoRequest.base64Image());
                fcmService.sendNotificationToMany(tokens, infoRequest, savedInfo, false);
                infoTracker.updateTimestamp(label, currentTimestamp);
            }
            log.info("same label not passed 30 secs");
            return;
        }

        // 새로운 라벨에 대한 정보 저장 및 즉시 알림 전송 - "발생"
        log.info("new label");
        Info savedInfo = save(infoRequest.cameraName(), currentTimestamp, label,
            infoRequest.base64Image());
        fcmService.sendNotificationToMany(tokens, infoRequest, savedInfo, true);

        // 현재 라벨에 대한 타임스탬프 업데이트
        infoTracker.updateTimestamp(label, currentTimestamp);
        log.info("send message finished");
    }


    public Info getInfo(Long infoId) {
        Info info = infoRepository.findById(infoId)
            .orElseThrow(() -> new RuntimeException("해당 ID 의 이상 상황 정보가 존재하지 않습니다."));
        return info;
    }

}
