package com.example.edumanage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @GetMapping("/basic")
    public String basicCheck() {
        return "OK";
    }
    
    @GetMapping("/text")
    public String simpleText() {
        return "Service is running";
    }
} 