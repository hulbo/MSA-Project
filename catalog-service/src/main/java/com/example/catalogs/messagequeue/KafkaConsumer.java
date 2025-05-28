package com.example.catalogs.messagequeue;

import com.example.catalogs.jpa.CatalogEntity;
import com.example.catalogs.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
    CatalogRepository repository;
    @Autowired
    public KafkaConsumer(CatalogRepository repository) {
        this.repository = repository;
    }

    // 상품 수량 변경
    @KafkaListener(topics = "${kafka.catalog.topic}", groupId = "${kafka.catalog.groupId}")
    public void updateQty(String kafkaMessage){
        log.info("kafka Message -> " + kafkaMessage);
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            // TypeReference<Map<Object, Object>>(){}
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        }catch (JsonProcessingException ex){
            log.error("Error parsing Kafka message: {}", kafkaMessage, ex); // 상세 로그
            // JSON 파싱 실패 시 더 이상 진행하지 않고 메서드 종료
            return;
        }

        // map이 null이거나 필수 키가 없는 경우를 방어적으로 처리
        if (map == null || !map.containsKey("productId") || !map.containsKey("qty")) {
            log.error("Invalid Kafka message format: Missing productId or qty. Message: {}", kafkaMessage);
            return; // 유효하지 않은 메시지는 처리하지 않음
        }

        String productId = (String)map.get("productId");
        Integer qty = (Integer)map.get("qty");
        if (productId == null || qty == null) {
            log.error("Kafka message contains null productId or qty after parsing. Message: {}", kafkaMessage);
            return;
        }

        // 수량 변경
        CatalogEntity entity = repository.findByProductId(productId);
        if(entity != null){
            if (entity.getStock() >= qty) { // 재고 부족 방지 로직 추가
                entity.setStock(entity.getStock() - qty); // 'stock' 대신 'qty' 사용
                repository.save(entity);
                log.info("Catalog updated for productId: {}, new stock: {}", productId, entity.getStock());
            } else {
                log.warn("Insufficient stock for productId: {}. Current stock: {}, requested qty: {}", productId, entity.getStock(), qty);
                // 재고 부족에 대한 추가적인 처리 (예: 재고 부족 토픽으로 메시지 전송)
            }
        } else {
            log.error("Kafka message received for non-existent productId: {}", productId);
        }
    }
}
