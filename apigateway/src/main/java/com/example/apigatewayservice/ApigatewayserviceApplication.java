package com.example.apigatewayservice;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApigatewayserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApigatewayserviceApplication.class, args);
	}

	/**
	 * Http 요청 및 응답을 저장하는 Repository
	 * Actuator를 통해 HTTP 트래픽을 모니터링할 수 있음
	 */
	@Bean
	public InMemoryHttpExchangeRepository createExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}
}
