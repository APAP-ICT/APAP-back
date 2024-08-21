package com.example.apapbackend.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;


    /**
     * S3 객체를 다운받아서 base64 형식의 파일로 변환하여 반환
     */
    public String downloadFileFromS3AsBase64(String s3ObjUrl) throws IOException {
        // URL에서 객체의 key 추출
        String key = extractKeyFromUrl(s3ObjUrl);

        // GetObjectRequest 생성
        GetObjectRequest getObjectRequest = new GetObjectRequest("apap-back", key);

        // S3 객체 다운로드
        try (S3Object s3Object = s3Client.getObject(getObjectRequest);
            InputStream s3ObjectStream = s3Object.getObjectContent();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = s3ObjectStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // ByteArrayOutputStream에서 바이트 배열로 변환
            byte[] fileBytes = byteArrayOutputStream.toByteArray();

            // Base64로 인코딩
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }

    /**
     * s3 객체 url 에서 key 를 추출
     */
    private String extractKeyFromUrl(String url) {
        // URL에서 key를 추출
        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();
            int lastSlashIndex = path.lastIndexOf('/');
            return (lastSlashIndex >= 0) ? path.substring(lastSlashIndex + 1) : path;
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL format: " + url, e);
        }
    }
}