package com.example.apapbackend.domain.fcm;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    boolean existsByEmail(String email);
    FCMToken findByEmail(String email);
}
