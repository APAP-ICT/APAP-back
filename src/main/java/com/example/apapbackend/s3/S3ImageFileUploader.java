package com.example.apapbackend.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import jdk.jfr.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class S3ImageFileUploader {

    private final AmazonS3Client amazonS3Client;
    private final String bucket = "apap-back";

    public S3ImageFileUploader(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    @Description("이미지를 받아서 S3에 저장한 후, 저장된 객체 URL 반환")
    public String uploadImageFromUrlToS3(String base64Image) throws IOException {
        // base64 로 인코딩한 이미지를 MultipartFile 로 변환
        MultipartFile file = convertBase64ToMultipartFile(base64Image);
        // S3에 저장할 고유한 키 생성(파일명으로 사용)
        String key = UUID.randomUUID().toString(); // 이거 쓰면 unique
//        String key = file.getOriginalFilename(); // 일단 이걸 키값으로 사용
        // 이미지 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // S3에 파일 업로드
        amazonS3Client.putObject(bucket, key, file.getInputStream(), metadata);

        // S3에 파일 업로드된 객체의 URL을 가져온다
        String objectUrl = amazonS3Client.getUrl("apap-back", key).toString();
        log.info("AWS 에 저장된 이미지 주소: {}", objectUrl);
        return objectUrl;
    }

    @Description("BASE64 인코딩된 이미지를 MultipartFile 로 변환")
    public MultipartFile convertBase64ToMultipartFile(String base64String) throws IOException {
        // Base64 문자열에서 "data:image/jpeg;base64," 같은 접두사를 제거
        String[] parts = base64String.split(",");
        String imageString = parts.length > 1 ? parts[1] : parts[0];

        // Base64 문자열에서 MIME 타입 추출
        String mimeType = parts[0].split(";")[0].split(":")[1];

        // Base64 문자열을 디코딩
        byte[] imageBytes = Base64.getDecoder().decode(imageString);

        // 파일 이름 생성 (여기서는 간단히 "image"로 설정)
        String fileName = "image";
        String[] mimeTypeParts = mimeType.split("/");
        if (mimeTypeParts.length == 2) {
            fileName += "." + mimeTypeParts[1];
        }

        // MultipartFile 객체로 변환
        // String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content)
        return new MockMultipartFile(fileName, fileName, mimeType, imageBytes);
    }
}

