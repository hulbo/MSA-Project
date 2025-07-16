package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import hulbo.common.util.ResponseUtil;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 관련 API 요청을 처리하는 컨트롤러 클래스.
 * RESTful 웹 서비스 엔드포인트를 정의하고 사용자 서비스 로직과 연동합니다.
 */
@Slf4j
@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    /**
     * UserController의 생성자.
     * UserService를 주입받아 사용자 관련 비즈니스 로직을 처리합니다.
     * @param userService 사용자 서비스 인스턴스
     */
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * 새로운 사용자를 생성하는 API 엔드포인트.
     * POST 요청을 통해 사용자 정보를 받아 UserDto로 변환 후 서비스 계층에 전달합니다.
     * @param user 생성할 사용자 정보를 담은 RequestUser 객체 (유효성 검사 포함)
     * @return 생성된 사용자 정보와 HTTP 상태 코드 (201 Created)를 포함하는 ResponseEntity
     */
    @PostMapping("/users")
    @Timed(value="users.createUser") // Prometheus/Grafana 모니터링을 위한 타이밍 지표 추가
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser user){
        // ModelMapper를 사용하여 RequestUser를 UserDto로 변환 (엄격한 매칭 전략 사용)
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);

        // 사용자 생성 서비스 호출 후, 반환된 UserDto를 ResponseUser로 변환
        ResponseUser responseUser = mapper.map(userService.createUser(userDto), ResponseUser.class);

        // 성공 응답 반환 (HTTP 201 Created)
        return ResponseUtil.CUSTOM(responseUser, HttpStatus.CREATED);

    }

    /**
     * 모든 사용자 목록을 조회하는 API 엔드포인트.
     * GET 요청을 통해 모든 사용자 정보를 조회하고 ResponseUser 리스트로 반환합니다.
     * @return 모든 사용자 정보 리스트와 HTTP 상태 코드 (200 OK)를 포함하는 ResponseEntity
     */
    @GetMapping("/users")
    @Timed(value="users.getUsers") // Prometheus/Grafana 모니터링을 위한 타이밍 지표 추가
    public ResponseEntity<List<ResponseUser>> getUsers() {
        // ModelMapper 설정
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // 모든 사용자 엔티티 조회
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        // 조회된 각 UserEntity를 ResponseUser로 변환하여 리스트에 추가
        userList.forEach(f -> {
            result.add(mapper.map(f, ResponseUser.class));
        });

        // 성공 응답 반환 (HTTP 200 OK)
        return ResponseUtil.OK(result);
    }

    /**
     * 특정 사용자 ID로 사용자 정보를 조회하는 API 엔드포인트.
     * GET 요청과 경로 변수(userId)를 통해 특정 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자의 고유 ID
     * @return 조회된 사용자 정보와 HTTP 상태 코드 (200 OK)를 포함하는 ResponseEntity
     */
    @GetMapping("/users/{userId}")
    @Timed(value="users.getUser") // Prometheus/Grafana 모니터링을 위한 타이밍 지표 추가
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){

        log.debug("#조회대상 ID: " + userId); // 디버그 로그 출력

        // ModelMapper 설정
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // 서비스 계층에서 userId를 통해 사용자 정보 조회
        UserDto userDto = userService.getUserByUserId(userId);

        // 조회된 UserDto를 ResponseUser로 변환
        ResponseUser result = mapper.map(userDto, ResponseUser.class);

        // 성공 응답 반환 (HTTP 200 OK)
        return ResponseUtil.OK(result);
    }
}
