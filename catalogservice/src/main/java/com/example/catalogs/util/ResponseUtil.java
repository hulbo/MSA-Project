package com.example.catalogs.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<String> OK(String message) {
        return ResponseEntity.ok(message);
    }

    public static <T> ResponseEntity<T> OK(T obj) {
        return ResponseEntity.status(HttpStatus.OK).body(obj);
    }

    public static ResponseEntity<String> BAD(String message) {
        return ResponseEntity.badRequest().body(message);
    }

    public static <T> ResponseEntity<T> CUSTOM(T obj, HttpStatus status) {
        return ResponseEntity.status(status).body(obj);
    }
}
