package com.example.apapbackend.domain.Info.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record InfoRequest(

    @NotBlank(message = "camera name is mandatory")
    String cameraName,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime localDateTime,
    @NotBlank(message = "base64 image is mandatory")
    String base64Image,
    @NotBlank(message = "label is mandatory")
    String label
) {

}
