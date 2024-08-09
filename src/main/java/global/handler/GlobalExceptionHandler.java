package global.handler;

import global.exception.BusinessException;
import global.exception.ErrorCode;
import global.exception.user.MemberDuplicateException;
import global.exception.user.MemberNotFoundException;
import global.response.ErrorResponseDto;
import global.response.ResponseMaker;
import io.jsonwebtoken.JwtException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * RuntimeException 을 상속받는 커스텀 에러 핸들러
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException e) {
        return ResponseMaker.createErrorResponse(ErrorCode.BAD_REQUEST,
            e.getMessage()); // TODO 수정 필요
    }

    /**
     * MethodArgumentNotValidException 에러 핸들러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> MethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(error.getDefaultMessage());
        });
        String message = String.join("\n", errors);
        return ResponseMaker.createErrorResponse(ErrorCode.BAD_REQUEST, message);
    }

    /**
     * JwtException 에러 핸들러
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponseDto> JwtException(JwtException e) {
        return ResponseMaker.createErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    /**
     * Member 도메인 관련 에러 핸들러
     */
    @ExceptionHandler(MemberDuplicateException.class)
    public ResponseEntity<ErrorResponseDto> UserDuplicateException(MemberDuplicateException e) {
        return ResponseMaker.createErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> UserNotFoundException(MemberNotFoundException e) {
        return ResponseMaker.createErrorResponse(ErrorCode.NOT_FOUND, e.getMessage());
    }

}
