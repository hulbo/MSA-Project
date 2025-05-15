package hulbo.msa.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    /**
     * JSON 응답을 생성하는 메서드
     * @param response HttpServletResponse 객체
     * @param status HTTP 응답 상태 코드 (예: 200, 403 등)
     * @param message 사용자에게 전달할 메시지
     * @param additionalData 추가 데이터 (선택 사항, 없으면 null 가능)
     */
    public static void sendJsonResponse(HttpServletResponse response, int status, String message, Map<String, Object> additionalData) {
        try {
            // HTTP 상태 코드 설정
            response.setStatus(status);

            // 응답 헤더 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // JSON 데이터를 생성
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", message);
            if (additionalData != null) {
                responseBody.putAll(additionalData);
            }

            // JSON 응답 전송
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
            response.getWriter().flush(); // 데이터 플러시
        } catch (IOException e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
        }
    }

}

