package com.example.apapbackend.fcm;

import com.example.apapbackend.Info.Info;
import com.example.apapbackend.Info.dto.InfoRequest;
import com.example.apapbackend.fcm.dto.FCMTokenRequest;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final FCMTokenRepository fcmTokenRepository;

    /**
     * FCMToken 을 이용한 푸시 알림 전송
     */
    public void sendNotificationToMany(List<String> tokens, InfoRequest infoRequest, Info savedInfo, Boolean isNew) {
        String newOrContinue = isNew ? " 발생" : " 지속중";

        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(savedInfo.getId())); // 이상 상황(Info) ID
        data.put("isNew", Boolean.toString(isNew)); // 이상 상황 발생 or 지속 여부

        // FCM에 보낼 메시지 빌드
        MulticastMessage fcmMessage = MulticastMessage.builder()
            .addAllTokens(tokens)
            .setNotification(com.google.firebase.messaging.Notification.builder()
                .setTitle(infoRequest.cameraName() + "에서 " + infoRequest.label() + newOrContinue) // 제목 ex) E-114 에서 안전모 미착용 발생
                .setBody(infoRequest.localDateTime().format(DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분"))) // 발생 시각 ex) 7월 7일 7시 7분
                .setImage(savedInfo.getImageUrl()) // 이상 상황 이미지 주소 URL
                .build())
            .putAllData(data)
            .build();

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
     * 이메일과 쌍으로 토큰 저장
     * 해당 이메일에 토큰 이미 존재할 경우 -> 토큰 업데이트
     */
    @Transactional
    public void saveToken(FCMTokenRequest tokenRequest) {
        if (!fcmTokenRepository.existsByEmail(tokenRequest.email())) {
            fcmTokenRepository.save(tokenRequest.toEntity());
            return;
        }
        FCMToken fcmToken = fcmTokenRepository.findByEmail(tokenRequest.email());
        fcmToken.updateToken(tokenRequest.token());
    }

    public List<String> getTokens() {
        List<FCMToken> tokenEntities = fcmTokenRepository.findAll();
        List<String> tokens = tokenEntities.stream().map(FCMToken::getToken).toList();
        return tokens;
    }
}
