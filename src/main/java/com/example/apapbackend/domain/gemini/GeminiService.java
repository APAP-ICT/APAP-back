package com.example.apapbackend.domain.gemini;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    public static final String GEMINI_PRO = "gemini-pro";
    public static final String GEMINI_ULTIMATE = "gemini-ultimate";
    public static final String GEMINI_FLASH = "gemini-1.5-flash";
    private final GeminiInterface geminiInterface;

    @Autowired
    public GeminiService(GeminiInterface geminiInterface) {
        this.geminiInterface = geminiInterface;
    }

    private GeminiResponse getCompletion(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_PRO, request);
    }

    public String getCompletion(String text) {
        GeminiRequest geminiRequest = new GeminiRequest(text);
        GeminiResponse response = getCompletion(geminiRequest);

        return response.getCandidates()
            .stream()
            .findFirst().flatMap(candidate -> candidate.getContent().getParts()
                .stream()
                .findFirst()
                .map(GeminiResponse.TextPart::getText))
            .orElse(null);
    }
    public GeminiResponse getCompletionWithImage(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_FLASH, request);
    }

    public String getCompletionWithImage(String text, GeminiRequest.InlineData inlineData) {
        GeminiRequest geminiRequest = new GeminiRequest(text, inlineData);
        GeminiResponse response = getCompletionWithImage(geminiRequest);

        return response.getCandidates()
            .stream()
            .findFirst().flatMap(candidate -> candidate.getContent().getParts()
                .stream()
                .findFirst()
                .map(GeminiResponse.TextPart::getText))
            .orElse(null);
    }

}
