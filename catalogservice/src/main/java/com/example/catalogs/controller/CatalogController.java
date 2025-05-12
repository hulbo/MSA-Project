package com.example.catalogs.controller;

import com.example.catalogs.jpa.CatalogEntity;
import com.example.catalogs.service.CatalogService;
import com.example.catalogs.util.JsonUtil;
import com.example.catalogs.util.ResponseUtil;
import com.example.catalogs.vo.ResponseCatalog;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class CatalogController {
    Environment env;
    CatalogService catalogService;

    public CatalogController(Environment env, CatalogService catalogService) {
        this.env = env;
        this.catalogService = catalogService;
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

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getUser() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Iterable<CatalogEntity> catalogsList = catalogService.getAllCatalogs();

        List<ResponseCatalog> result = new ArrayList<>();
        catalogsList.forEach(f -> {
            result.add(mapper.map(f, ResponseCatalog.class));
        });

        return ResponseUtil.OK(result);
    }
}
