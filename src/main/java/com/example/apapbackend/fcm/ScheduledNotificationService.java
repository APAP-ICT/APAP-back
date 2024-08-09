package com.example.apapbackend.fcm;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledNotificationService {

    private final FCMService fcmService;

    // 디바이스 토큰 기반 메시징
//    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void sendNotification() {
        List<String> tokens = fcmService.getTokens();

        // 전송할 알림 정보
        String title = "notification from APAP-backend server";
        String body = "This is a notification sent every 30 secs.";

        for (String token : tokens) {
            fcmService.sendNotificationToOne(token, title, body);
        }
    }

    // 주제 기반 메시징
//    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void sendNotificationToTopic() {
        // 주제와 알림 정보
        String topic = "test-topic"; // 구독할 주제 이름
        String title = "notification from APAP-backend server";
        String body = "This is a notification sent every 30 secs.";

        fcmService.sendNotificationToTopic(topic, title, body);
    }

}
