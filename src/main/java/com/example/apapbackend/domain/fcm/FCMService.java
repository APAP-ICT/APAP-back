package com.example.apapbackend.domain.fcm;

import com.example.apapbackend.domain.Info.Info;
import com.example.apapbackend.domain.Info.dto.InfoRequest;
import com.example.apapbackend.domain.fcm.dto.FCMTokenRequest;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private final FCMTokenRepository fcmTokenRepository;

    /**
     * FCMToken 을 이용한 푸시 알림 전송
     */
    public void sendNotificationToMany(List<String> tokens, InfoRequest infoRequest, Info savedInfo,
        Boolean isNew) {
        String newOrContinue = isNew ? " 발생" : " 지속중";

        Map<String, String> data = new HashMap<>();
        data.put("infoId", String.valueOf(savedInfo.getId())); // 이상 상황(Info) ID
        data.put("isNew", Boolean.toString(isNew)); // 이상 상황 발생 or 지속 여부
        data.put("title", infoRequest.cameraName() + "에서 " + infoRequest.label() + newOrContinue); // 제목
        data.put("body", infoRequest.localDateTime().format(DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분"))); // 본문
        data.put("imageUrl", savedInfo.getImageUrl()); // 이미지 URL

        // FCM에 보낼 메시지 빌드
        MulticastMessage fcmMessage = MulticastMessage.builder()
            .addAllTokens(tokens)
            .putAllData(data) // 추가 데이터
            .build();

        log.info("fcmMessage: {}", fcmMessage);
        try {
            // 메시지 전송
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(fcmMessage);
            List<String> successfulIds = new ArrayList<>();
            List<String> failedIds = new ArrayList<>();

            for (SendResponse sendResponse : response.getResponses()) {
                if (sendResponse.isSuccessful()) {
                    successfulIds.add(sendResponse.getMessageId());
                } else {
                    failedIds.add(sendResponse.getException().getMessage());
                }
            }

            System.out.println(
                "Successfully sent messages with IDs: " + String.join(", ", successfulIds));
            if (!failedIds.isEmpty()) {
                System.err.println("Failed to send messages: " + String.join(", ", failedIds));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 이메일과 쌍으로 토큰 저장 해당 이메일에 토큰 이미 존재할 경우 -> 토큰 업데이트
     */
    @Transactional
    public void saveToken(FCMTokenRequest tokenRequest) {
        log.info("fcmTokenRequest: {}", tokenRequest);
        if (!fcmTokenRepository.existsByEmail(tokenRequest.email())) {
            FCMToken savedToken = fcmTokenRepository.save(tokenRequest.toEntity());
            log.info("fcmToken saved: {}", savedToken);
            return;
        }
        FCMToken fcmToken = fcmTokenRepository.findByEmail(tokenRequest.email());
        fcmToken.updateToken(tokenRequest.token());
        log.info("fcmToken updated: {}", fcmToken);

    }

    public List<String> getTokens() {
        List<FCMToken> tokenEntities = fcmTokenRepository.findAll();
        List<String> tokens = tokenEntities.stream().map(FCMToken::getToken).toList();
        return tokens;
    }
}
