package com.example.apapbackend.domain.Info;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class InfoTracker {

    private final Map<String, LocalDateTime> labelTimestamps = new HashMap<>();

    public LocalDateTime getTimestamp(String label) {
        return labelTimestamps.get(label);
    }

    public void updateTimestamp(String label, LocalDateTime timestamp) {
        labelTimestamps.put(label, timestamp);
    }
}