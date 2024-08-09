package com.example.apapbackend.fcm;

import com.example.apapbackend.fcm.dto.FCMTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class FCMTokenController {

    private final FCMService fcmService;

    @PostMapping("/token")
    @Description("FCM 토큰 받아서 DB 에 저장")
    public void getFCMToken(@Valid @RequestBody FCMTokenRequest fcmTokenRequest) {
        fcmService.saveToken(fcmTokenRequest);
    }
}
