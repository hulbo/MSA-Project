package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.util.JsonUtil;
import com.example.userservice.util.ResponseUtil;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final UserService userService;

    @Autowired
    private Greeting greeting;

    @Autowired
    public UserController(Environment env, UserService userService){
        this.env = env;
        this.userService = userService;
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
        //return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @GetMapping("/welcome")
    public String welcome(){
        //return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);

        ResponseUser responseUser = mapper.map(userService.createUser(userDto), ResponseUser.class);

        return ResponseUtil.CUSTOM(responseUser, HttpStatus.CREATED);

    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUser() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(f -> {
            result.add(mapper.map(f, ResponseUser.class));
        });


        return ResponseUtil.OK(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser result = new ResponseUser();
        result = mapper.map(userDto, ResponseUser.class);

        return ResponseUtil.OK(result);
    }
}
