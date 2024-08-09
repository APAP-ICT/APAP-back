package com.example.apapbackend.Info;

import com.example.apapbackend.Info.dto.InfoRequest;
import com.example.apapbackend.fcm.FCMService;
import com.example.apapbackend.fcm.FCMToken;
import com.example.apapbackend.fcm.FCMTokenRepository;
import com.example.apapbackend.s3.S3ImageFileUploader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final S3ImageFileUploader s3ImageFileUploader;
    private final InfoRepository infoRepository;
    private final InfoTracker infoTracker;
    private final FCMService fcmService;
    private final FCMTokenRepository fcmTokenRepository;

    /**
     * 객체 정보 저장
     */
    public Info save(String cameraName, LocalDateTime localDateTime, String label,
        String base64Image) {
        try {
            String objUrl = s3ImageFileUploader.uploadImageFromUrlToS3(base64Image);
            Info info = new Info(cameraName, localDateTime, label, objUrl);
            return infoRepository.save(info);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이상 상황 정보 저장 중 에러 발생");
        }
    }

    /**
     * 모든 객체 탐지 정보 조회
     * 시작 & 끝 기간, 카메라, 이상상황을 조건으로 받음 - null 인 경우엔 조건을 적용하지 않음
     */
    public List<Info> getInfos(LocalDateTime startDate, LocalDateTime endDate, String cameraName, String label) {
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
        List<String> tokens = fcmTokens.stream().map(fcmToken -> fcmToken.getToken()).toList();
        // 현재 라벨과 타임스탬프
        String label = infoRequest.label();
        LocalDateTime currentTimestamp = infoRequest.localDateTime();
        // 현재 라벨에 대한 직전 타임스탬프
        LocalDateTime lastTimestamp = infoTracker.getTimestamp(label);

        // 알림을 보낼 조건 확인
        if (lastTimestamp != null) {
            Duration duration = Duration.between(lastTimestamp, currentTimestamp);
            // 같은 라벨이 30초 이상 지났다면 알림 전송
            if (duration.getSeconds() >= 30) {
                Info savedInfo = save(infoRequest.cameraName(), infoRequest.localDateTime(),
                    infoRequest.label(),
                    infoRequest.base64Image());
                fcmService.sendNotificationToMany(
                    tokens, savedInfo.getId(), infoRequest.label(), "직전 이상 상황이 계속되고 있습니다.", savedInfo.getImageUrl()
                );
                infoTracker.updateTimestamp(label, currentTimestamp);
            }
            return;
        }
        // 새로운 라벨에 대한 정보 저장
        Info savedInfo = save(infoRequest.cameraName(), infoRequest.localDateTime(),
            infoRequest.label(),
            infoRequest.base64Image());
        // 새로운 라벨이라면 즉시 알림 전송
        fcmService.sendNotificationToMany(
            tokens, savedInfo.getId(), infoRequest.label(), "새로운 이상 상황이 발생했습니다.", savedInfo.getImageUrl()
        );
        // 현재 라벨에 대한 타임스탬프 업데이트
        infoTracker.updateTimestamp(label, currentTimestamp);
    }

    public Info getInfo(Long infoId) {
        Info info = infoRepository.findById(infoId)
            .orElseThrow(() -> new RuntimeException("해당 ID 의 이상 상황 정보가 존재하지 않습니다."));
        return info;
    }

}
