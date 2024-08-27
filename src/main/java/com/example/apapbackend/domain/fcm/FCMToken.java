package com.example.apapbackend.domain.fcm;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FCMToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private String token;

    protected FCMToken() {
    }

    public FCMToken(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "FCMToken{" +
            "id=" + id +
            ", email='" + email + '\'' +
            ", token='" + token + '\'' +
            '}';
    }
}
