package com.example.apapbackend.global.gemini;

import com.example.apapbackend.global.s3.S3Service;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    public static final String GEMINI_PRO = "gemini-pro";
    public static final String GEMINI_ULTIMATE = "gemini-ultimate";
    public static final String GEMINI_FLASH = "gemini-1.5-flash";
    private final GeminiInterface geminiInterface;
    private final S3Service s3Service;

    private GeminiResponse getCompletion(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_PRO, request);
    }

    public String getCompletion(String text) {
        GeminiRequest geminiRequest = new GeminiRequest(text);
        GeminiResponse response = getCompletion(geminiRequest);

        return response.getCandidates().stream().findFirst().flatMap(
            candidate -> candidate.getContent().getParts().stream().findFirst()
                .map(GeminiResponse.TextPart::getText)).orElse(null);
    }

    public GeminiResponse getCompletionWithImage(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_FLASH, request);
    }

    public String getDescription(String s3ObjUrl) throws IOException {
        // S3 객체를 로컬 파일로 다운로드
        String base64Image = s3Service.downloadFileFromS3AsBase64(s3ObjUrl);

        GeminiRequest.InlineData inlineData = new GeminiRequest.InlineData("image/jpeg", base64Image);

        String description = getCompletionWithImage(
            "평소와 다른 이상 상황이 존재하는지 판단하고, 이상 상황이 존재하면 해당 상황을 설명하고 대처 방법을 알려줘", inlineData);
        log.info("description: {}", description);
        return description;
    }

    public String getCompletionWithImage(String text, GeminiRequest.InlineData inlineData) {
        GeminiRequest geminiRequest = new GeminiRequest(text, inlineData);
        GeminiResponse response = getCompletionWithImage(geminiRequest);

        return response.getCandidates().stream().findFirst().flatMap(
            candidate -> candidate.getContent().getParts().stream().findFirst()
                .map(GeminiResponse.TextPart::getText)).orElse(null);
    }

}