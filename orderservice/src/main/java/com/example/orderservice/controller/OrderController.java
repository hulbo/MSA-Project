package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.JsonUtil;
import com.example.orderservice.util.ResponseUtil;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Slf4j
public class OrderController {
    Environment env;
    OrderService orderService;

    public OrderController(Environment env, OrderService orderService) {
        this.env = env;
        this.orderService = orderService;
    }

    @GetMapping("/healthCheck")
    public String status() {
        // 서비스 상태 정보를 Map에 저장
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("message", "It's Working in User Service");
        statusMap.put("localServerPort", env.getProperty("local.server.port"));
        statusMap.put("serverPort", env.getProperty("server.port"));
        statusMap.put("gatewayIp", env.getProperty("gateway.ip"));
        statusMap.put("greetingMessage", env.getProperty("greeting.message"));
        statusMap.put("tokenSecret", env.getProperty("token.secret"));
        statusMap.put("tokenExpirationTime", env.getProperty("token.expiration_time"));
        statusMap.put("repo:location", env.getProperty("hulbo.location"));
        statusMap.put("repo:version", env.getProperty("hulbo.version"));
        statusMap.put("repo:message", env.getProperty("hulbo.message"));
        statusMap.put("spring.application.name", env.getProperty("spring.application.name"));

        // 공통 JsonUtil을 사용하여 JSON 형식으로 반환
        return JsonUtil.toJson(statusMap);
    }

    @GetMapping("/info")
    public String info(){
        return env.getProperty("greeting.message");
        //return greeting.getMessage();
    }

    @GetMapping("/welcome")
    public String welcome(){
        return env.getProperty("greeting.message");
        //return greeting.getMessage();
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        log.info("Before add orders data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
        OrderDto createdOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        log.info("After added orders data");
        return ResponseUtil.CUSTOM(responseOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(f -> {
            result.add(new ModelMapper().map(f, ResponseOrder.class));
        });
        log.info("Add retrieved orders data");

        return ResponseUtil.OK(result);
    }
}
