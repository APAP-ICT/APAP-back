package com.example.apapbackend.Info;

import com.amazonaws.Response;
import com.example.apapbackend.S3.S3ImageFileUploader;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final S3ImageFileUploader s3ImageFileUploader;
    private final InfoRepository infoRepository;

    /**
     * 객체 정보 저장
     *
     * @param localDateTime
     * @param label
     * @param base64Image
     * @return DB 에 저장한 객체 정보 반환
     */
    public ResponseEntity<Info> save(LocalDateTime localDateTime, String label,
        String base64Image) {
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

    /**
     * 모든 객체 탐지 정보 조회
     *
     * @return
     */
    public ResponseEntity<List<Info>> getInfos() {
        List<Info> infos = infoRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(infos);
    }
}
