package com.example.apapbackend.global.response;

/**
 * 메시지, "데이터" 전달
 */
public record ResultResponseDto<T>(T data) {

}
