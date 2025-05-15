package hulbo.msa.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // JSON 키 정렬 설정 추가
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    // Map을 JSON 문자열로 변환하는 공통 메서드 (Pretty Print + 정렬 적용)
    public static String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (Exception e) {
            return "{\"error\": \"JSON 변환 실패\"}";
        }
    }
}

