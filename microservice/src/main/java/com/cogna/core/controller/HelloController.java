package com.cogna.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from cogna microservice";
    }

    @GetMapping("/health")
    public String health() {
        return "OK - " + Instant.now().toString();
    }
}
