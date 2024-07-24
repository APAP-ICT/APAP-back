package com.example.apapbackend.fcm;

import com.example.apapbackend.fcm.dto.FCMTokenRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final FCMTokenRepository tokenRepository;

    // 디바이스 토큰 기반 메시징
    public void sendNotification(String token, String title, String body) {
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //주제 기반 메시징
    public void sendNotificationToTopic(String topic, String title, String body) {
        Message message = Message.builder()
            .setTopic(topic)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToken(FCMTokenRequest tokenRequest) {
        FCMToken token = tokenRequest.toEntity(tokenRequest);
        tokenRepository.save(token);
    }
}
