package com.example.apapbackend.fcm;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledNotificationService {

    private final FCMService fcmService;
    private final FCMTokenRepository fcmTokenRepository;

    // 디바이스 토큰 기반 메시징
    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void sendNotification() {
        List<String> tokens = getTokens();

        // 전송할 알림 정보
        String title = "Scheduled Notification";
        String body = "This is a notification sent every 30 secs.";

        for (String token : tokens) {
            fcmService.sendNotification(token, title, body);
        }
    }

    // 주제 기반 메시징
    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void sendNotificationToTopic() {
        // 주제와 알림 정보
        String topic = "test-topic"; // 구독할 주제 이름
        String title = "Scheduled Notification";
        String body = "This is a notification sent every 30 secs.";

        fcmService.sendNotificationToTopic(topic, title, body);
    }

    private List<String> getTokens() {
        List<FCMToken> tokenEntities = fcmTokenRepository.findAll();
        List<String> tokens = tokenEntities.stream().map(FCMToken::getToken).toList();
        return tokens;
    }
}
