package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import hulbo.common.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
@Slf4j
public class OrderController {
    OrderService orderService;
    KafkaProducer kafkaProducer;
    OrderProducer orderProducer;
    Environment env;

    @Autowired
    public OrderController(OrderService orderService, KafkaProducer kafkaProducer
            , Environment env, OrderProducer orderProducer) {
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
        this.env = env;
    }

    @PostMapping("/{userId}/orders")
    @Timed(value="order.createOrder")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        log.info("orders data 생성 전");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        /* JPA 방식 */
        /*OrderDto createdOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);*/

        /* kafka 방식 */
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
        
        /* 카푸카로 주문정보 전송 -> 카탈로그에서 처리진행함 */
        kafkaProducer.send(env.getProperty("kafka.catalog.topic"), orderDto);

        /* 카푸카로 주문정보 정송 -> DB 에 저장됨 */
        orderProducer.send("write_topic_maria_t_sc_orders", orderDto);

        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);
        log.info("orders data 생성 후");
        return ResponseUtil.CUSTOM(responseOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/orders")
    @Timed(value="order.userGetOrders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("orders data 조회 전");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(f -> {
            result.add(new ModelMapper().map(f, ResponseOrder.class));
        });
        log.info("orders data 조회 후");

        return ResponseUtil.OK(result);
    }
}
