package com.example.userservice.filter;

// 사용자 서비스와 요청 데이터 클래스 import

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import hulbo.common.util.ResponseUtil;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

@Slf4j
// 커스텀 인증 필터 클래스 정의 (Spring Security의 UsernamePasswordAuthenticationFilter 확장)
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // 사용자 서비스 및 환경 설정 필드 정의
    private final UserService userService;
    private final Environment env;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 생성자를 통해 필터에 필요한 객체를 주입
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env, BCryptPasswordEncoder bCryptPasswordEncoder) {
        super.setAuthenticationManager(authenticationManager); // 인증 관리자 설정
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService; // 사용자 서비스 설정
        this.env = env; // 환경 설정 주입
    }

    /**
     * 인증 요청을 처리하는 메서드.
     * 사용자가 전송한 인증 데이터를 받아서 AuthenticationManager를 통해 인증을 수행.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 요청의 InputStream에서 JSON 데이터를 읽어와 RequestLogin 객체로 변환
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            // 사용자 조회: 입력된 이메일로 사용자 검색
            UserDetails userDetails = userService.loadUserByUsername(creds.getEmail());
            if (userDetails == null || !bCryptPasswordEncoder.matches(creds.getPassword(), userDetails.getPassword())) {
                throw new AuthenticationException("사용자 인증 실패") {};
            }

            log.debug("CustomAuthenticationFilter - 사용자 인증 시도: " + creds.getEmail());

            // AuthenticationManager를 통해 인증 진행
            // UsernamePasswordAuthenticationToken을 직접 생성하여 반환
            return new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    creds.getPassword(),
                    userDetails.getAuthorities()
            );
        } catch (IOException e) {
            throw new RuntimeException("JSON 요청 데이터를 처리하는 중 오류 발생", e);
        }
    }

    /**
     * 인증 성공 시 호출되는 메서드.
     * 인증 결과를 처리하고 클라이언트에게 성공 응답을 반환.
     * 토큰생성
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 사용자 조회
        log.debug("로그인 성공 사용자 email :: " + authResult.getName());
        String userId = authResult.getName();
        UserDto userDto = userService.getUserDetailByEmail(userId);
        log.debug("사용자 조회 ID :: " + userDto.getUserId());
        log.debug("사용자 조회 명 :: " + userDto.getName());

        // JWT 토큰생성
        log.debug("토큰 시간 :: " + env.getProperty("token.expiration_time"));
        log.debug("토큰 암호 :: " + env.getProperty("token.secret"));

        // 토큰 암호 생성 HS512로 생성 후 application.yml에 복사해서 사용함
        // HS512에 적합한 안전한 키 생성
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        // Base64로 키를 문자열로 변환
        String secretKey = Base64.getEncoder().encodeToString(keyBytes);
        log.debug("토큰 암호 생성 :: " + secretKey);

        String token = Jwts.builder()
                .setSubject(userDto.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        log.debug("토큰 생성된값 :: " + token);
        log.debug("토큰 사용자ID :: " + userDto.getUserId());

        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());

        log.debug("Response Header 'token': " + response.getHeader("token"));
        log.debug("Response Header 'userId': " + response.getHeader("userId"));

        // 쿠키에서 JSESSIONID 삭제를 유도
        response.setHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Secure; Max-Age=0");

        // HTTP 응답 상태를 200 (OK)으로 설정
        ResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, "로그인 성공", null);

        // 부모 클래스의 인증 성공 처리 로직 호출
        super.successfulAuthentication(request, response, chain, authResult);
    }

    /**
     * 인증 실패 시 호출되는 메서드.
     * 인증 실패 정보를 처리하고 클라이언트에게 실패 응답을 반환.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // HTTP 응답 상태를 401 (Unauthorized)로 설정
        ResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "로그인 실패", null);

        // 부모 클래스의 인증 실패 처리 로직 호출
        super.unsuccessfulAuthentication(request, response, failed);
    }
}