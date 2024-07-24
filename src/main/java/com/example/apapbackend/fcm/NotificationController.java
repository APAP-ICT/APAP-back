package com.example.apapbackend.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final FCMService fcmService;

    // 클라이언트가 요청 보내면 알림 보냄 - 현재는 사용할 이유 X
    @PostMapping("/send")
    public void sendNotification(@RequestBody NotificationRequest notificationRequest) {
        fcmService.sendNotification(notificationRequest.getToken(), notificationRequest.getTitle(), notificationRequest.getBody());
    }
}
