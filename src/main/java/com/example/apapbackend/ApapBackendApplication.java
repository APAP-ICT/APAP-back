package com.example.apapbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApapBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApapBackendApplication.class, args);
    }

}
