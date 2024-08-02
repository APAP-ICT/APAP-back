package com.example.apapbackend.Info.dto;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record InfoRequest(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime,
    String label,
    String base64Image
){

}
