package com.example.apapbackend.fcm;

import com.example.apapbackend.fcm.dto.FCMTokenRequest;
import com.example.apapbackend.fcm.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final FCMService fcmService;

    @PostMapping("/send")
    @Description("클라이언트가 요청 보내면 알림 보냄 - 현재는 사용할 이유 X")
    public void sendNotification(@RequestBody NotificationRequest notificationRequest) {
        fcmService.sendNotification(notificationRequest.token(), notificationRequest.title(),
            notificationRequest.body());
    }
    
    @PostMapping("/token")
    @Description("FCM 토큰 받아서 DB 에 저장")
    public void getFCMToken(@RequestBody FCMTokenRequest fcmTokenRequest) {
        fcmService.saveToken(fcmTokenRequest);
    }
}
