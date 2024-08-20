package com.example.apapbackend.global.exception.user;

public class MemberDuplicateException extends RuntimeException {

    public MemberDuplicateException(String email) {
        super(email + " 이메일을 가진 유저가 이미 존재합니다.");
    }

}
