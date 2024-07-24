package com.example.apapbackend.fcm.dto;

public record NotificationRequest(
    String token,
    String title,
    String body
) {
}
