package com.example.apapbackend.fcm;

import com.example.apapbackend.fcm.dto.FCMTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class FCMTokenController {

    private final FCMService fcmService;

    @PostMapping
    @Operation(summary = "이메일과 함께 토큰 전송", description = "해당 이메일의 토큰을 중복을 검사하여 저장합니다.")
    public void getFCMToken(@Valid @RequestBody FCMTokenRequest fcmTokenRequest) {
        fcmService.saveToken(fcmTokenRequest);
    }
}
