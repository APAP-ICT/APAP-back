package com.example.apapbackend.Info;

import com.example.apapbackend.S3.S3ImageFileUploader;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final S3ImageFileUploader s3ImageFileUploader;
    private final InfoRepository infoRepository;

    public ResponseEntity<Info> save(LocalDateTime localDateTime, String label, String base64Image) {
        try {
            String objUrl = s3ImageFileUploader.uploadImageFromUrlToS3(base64Image);
            Info info = new Info(localDateTime, label, objUrl);
            Info saveInfo = infoRepository.save(info);

            return ResponseEntity.status(HttpStatus.OK).body(saveInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
