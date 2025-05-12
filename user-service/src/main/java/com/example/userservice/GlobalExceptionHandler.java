package com.example.userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> validation = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validation.put(fieldName, errorMessage);
        });
        errors.put("validation", validation);
        errors.put("message", "데이터 유효성이 잘못되었습니다.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        // 상세 메시지에서 원인 파악
        String rootCause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "알 수 없는 오류";
        if (rootCause.contains("unique constraint")) {
            errors.put("message", "중복된 데이터로 인해 저장할 수 없습니다. 동일한 값이 이미 존재합니다.");
        } else if (rootCause.contains("foreign key")) {
            errors.put("message", "참조된 데이터가 존재하지 않아 저장할 수 없습니다.");
        } else if (rootCause.contains("not null")) {
            errors.put("message", "필수 값이 누락되어 저장할 수 없습니다. 필요한 값을 입력해주세요.");
        } else {
            errors.put("message", "데이터 무결성 제약 조건을 위반했습니다.");
        }

        // 로그 출력
        log.error("Data Integrity Violation: {}", rootCause);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "존재하지 않는 사용자 입니다.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        log.error("An error :: ", ex);
        errors.put("message", "오류가 발생하였습니다.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
