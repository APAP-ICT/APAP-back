package com.example.apapbackend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.example.apapbackend.global.gemini.GeminiRequest;
import com.example.apapbackend.global.gemini.GeminiService;
import com.example.apapbackend.global.s3.S3Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GeminiTests {
    @Autowired
    private GeminiService service;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private S3Service s3Service;

    @Test
    @Description("텍스트만 사용해 요청")
    void getCompletion() {
        String text = service.getCompletion("서울 맛집을 추천해줘");
        System.out.println(text);
    }

    @Test
    @Description("텍스트 + 이미지 사용해 요청")
    void describeAnImage() throws Exception {
        GeminiRequest.InlineData inlineData = new GeminiRequest.InlineData(
            "image/jpeg",
            Base64.getEncoder().encodeToString(
                Files.readAllBytes(Path.of("src/main/resources/", "img_1.png")))
        );

        String text = service.getCompletionWithImage(
            "이 이미지는 항만에서 찍힌 사진이다. 평소와 다른 이상 상황이 있는지 판단하고 이상 상황이 존재하면 해당 상황을 설명하고 해당 상황을 대처하기 위한 방법을 추천해줘",
            inlineData
        );
        System.out.println(text);
    }

    @Test
    public void testGetDescription() throws IOException {
        // 준비 단계: S3 객체 Mock 설정
        String s3ObjUrl = "https://apap-back.s3.ap-northeast-2.amazonaws.com/01763b79-9c8b-49a3-8bdc-474dbca72333";
        String description = service.getDescription(s3ObjUrl);
        System.out.println("description = " + description);
    }

}
