package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public static class Config {
        // 필요한 설정 필드 추가
    }

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    // JWT 토큰 정검
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                // 접근불가
                return onError(exchange, "no authorization", HttpStatus.UNAUTHORIZED);
            }
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if(!isJwtValid(authorizationHeader)){
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String authorizationHeader) {
        log.debug("토큰 체크");
        String token = authorizationHeader.replace("Bearer", "");

        log.debug("토큰 입력받은 토큰 :: " + token);

        String subject = "";
        // JWT 토큰에서 Claims 값을 추출
        try {
            String secret = env.getProperty("token.secret");
            log.debug("토큰 암호 :: " + secret);

            Claims claims = Jwts.parserBuilder().setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // JWT 토큰에서 subject 값을 추출
            subject = claims.getSubject();
        } catch (Exception e){
            log.error("subject 값이 없음");
            return false;
        }

        log.debug("가져온 SUBJECT 값 :: " + subject);
        if(!StringUtils.hasText(subject)){
            return false;
        }

        return true;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = (ServerHttpResponse) exchange.getResponse(); // 응답 객체 가져오기
        response.setStatusCode(httpStatus); // HTTP 상태 코드 설정

        log.error(err); // 에러 로그 출력

        return response.setComplete();
    }
}
