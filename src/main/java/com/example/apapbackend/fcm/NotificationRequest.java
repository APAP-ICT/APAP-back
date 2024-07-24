package com.example.apapbackend.fcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {

    private String token;
    private String title;
    private String body;

    public NotificationRequest() {
    }

    public NotificationRequest(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }
}
