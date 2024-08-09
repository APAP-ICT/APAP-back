package com.example.apapbackend.fcm.dto;

import com.example.apapbackend.fcm.FCMToken;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FCMTokenRequest(

    @NotBlank
    String token
) {
    public FCMToken toEntity() {
        return new FCMToken(this.token);
    }
}
