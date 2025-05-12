package com.example.userservice.security;

import com.example.userservice.filter.CustomAuthenticationFilter;
// 사용자 서비스 관련 클래스 import
import com.example.userservice.service.UserService;

// Spring 관련 설정 클래스 import
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// 환경 변수 설정을 위한 클래스 import (Spring Framework의 Environment로 수정)

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class WebSecurity {

    // UserService와 암호화 처리를 위한 Encoder, 환경 변수 객체 선언
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;
    private final AuthenticationConfiguration authenticationConfiguration;

    // 생성자를 통해 의존성을 주입받아 초기화
    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationConfiguration authenticationConfiguration) {
        this.env = env; // 환경 변수 설정
        this.userService = userService; // 사용자 서비스 주입
        this.bCryptPasswordEncoder = bCryptPasswordEncoder; // 비밀번호 암호화기 설정
        this.authenticationConfiguration = authenticationConfiguration; // 인증 설정 객체 주입
    }

    /**
     * SecurityFilterChain을 정의하여 Spring Security의 핵심 설정 구성.
     * 인증 및 권한 부여, 필터 설정, CSRF 비활성화 등 정의.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호 비활성화: REST API 환경에서는 필요하지 않음
        http.csrf(AbstractHttpConfigurer::disable);

        // 세션 정책을 무상태로 설정하여 세션을 비활성화(Header에 JSESSIONID 사용안하게)
        http.sessionManagement(session
                -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /**
         * 권한 설정: 인증과 권한 관리는 필요하지 않으므로 제거.
         * 인증된 사용자는 이 서비스로 로그인 요청만 처리합니다.
         */
        // TODO:HULBO:토큰인증 관련 추가해야 할듯..(api-gate-way 에서 처리하긴하지만...)
        http.authorizeHttpRequests(auth -> auth
                //.requestMatchers("/actuator/**").permitAll() // 로그인 요청은 모든 사용자에게 허용
                .requestMatchers("/login").permitAll() // 로그인 요청은 모든 사용자에게 허용
                .anyRequest().permitAll() // 나머지 요청은 인증과 관계없이 허용
        );

        /**
         * AccessDeniedHandler 및 AuthenticationEntryPoint를 주석 처리.
         * 이 서비스는 토큰 체크를 하지 않으므로 인증 및 권한 부족 처리 로직 불필요.
         */
        // http.exceptionHandling(exception -> exception
        //         .authenticationEntryPoint((request, response, authException) -> {
        //             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //             response.setContentType("application/json");
        //             response.setCharacterEncoding("UTF-8");
        //
        //             Map<String, String> errors = new HashMap<>();
        //             errors.put("message", "인증되지 않은 사용자입니다.");
        //             response.getWriter().write(new ObjectMapper().writeValueAsString(errors));
        //         })
        //         .accessDeniedHandler((request, response, accessDeniedException) -> {
        //             response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        //             response.setContentType("application/json");
        //             response.setCharacterEncoding("UTF-8");
        //
        //             Map<String, String> errors = new HashMap<>();
        //             errors.put("message", "접근이 거부되었습니다. 권한이 없습니다.");
        //             errors.put("error", accessDeniedException.getMessage());
        //             response.getWriter().write(new ObjectMapper().writeValueAsString(errors));
        //         })
        // );

        /**
         * 인증 필터 설정: 로그인 처리 필터를 유지합니다.
         */
        http.addFilterBefore(customAuthenticationFilter(authenticationManager(authenticationConfiguration)),
                UsernamePasswordAuthenticationFilter.class);
        return http.build(); // 설정 완료 후 SecurityFilterChain 반환

    }

    /**
     * AuthenticationManager를 정의하여 인증 관리.
     * Spring Security에서 제공하는 AuthenticationConfiguration을 사용하여 인증 관리자 생성.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // 기본 AuthenticationManager 생성 및 반환
    }

    /**
     * CustomAuthenticationFilter를 정의하여 로그인 처리 로직 구성.
     * 사용자 서비스(UserService), 환경 변수(Environment), 인증 관리자(AuthenticationManager)를 주입.
     */
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        // 필터 생성 및 설정
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager, userService, env, bCryptPasswordEncoder);
        filter.setFilterProcessesUrl("/login"); // 로그인 URL 설정
        return filter; // 필터 반환
    }
}