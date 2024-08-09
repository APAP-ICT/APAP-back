package com.example.apapbackend.fcm;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    boolean existsByToken(String token);
}
