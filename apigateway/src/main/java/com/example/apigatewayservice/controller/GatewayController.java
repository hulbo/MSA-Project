package com.example.apigatewayservice.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GatewayController {

    private final Environment env;

    public GatewayController(Environment env) {
        this.env = env;
    }

    @GetMapping("/healthCheck")
    public String status(){
        return String.format("It's Working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", gateway ip=" + env.getProperty("gateway.ip")
                + ", message=" + env.getProperty("greeting.message")
                + ", token secret=" + env.getProperty("token.secret")
                + ", token expiration time=" + env.getProperty("token.expiration_time"));
    }

    @GetMapping("/info")
    public String info(){
        return env.getProperty("greeting.message");
    }
}
