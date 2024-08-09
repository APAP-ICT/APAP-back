package com.example.apapbackend.fcm;

import com.example.apapbackend.fcm.dto.FCMTokenRequest;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final FCMTokenRepository fcmTokenRepository;

    // 디바이스 토큰 기반 메시징
    public void sendNotificationToOne(String token, String title, String body) {
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

    public void sendNotificationToMany(List<String> tokens, String label, String message,
        String s3ImageUrl) {
        // FCM에 보낼 메시지 빌드
        MulticastMessage fcmMessage = MulticastMessage.builder()
            .addAllTokens(tokens)
            .setNotification(com.google.firebase.messaging.Notification.builder()
                .setTitle(label)
                .setBody(message)
                .setImage(s3ImageUrl)
                .build())
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

    @Transactional
    public void saveToken(FCMTokenRequest tokenRequest) {
        if (!fcmTokenRepository.existsByToken(tokenRequest.token())) {
            fcmTokenRepository.save(tokenRequest.toEntity());
        }
    }

    public List<String> getTokens() {
        List<FCMToken> tokenEntities = fcmTokenRepository.findAll();
        List<String> tokens = tokenEntities.stream().map(FCMToken::getToken).toList();
        return tokens;
    }
}
