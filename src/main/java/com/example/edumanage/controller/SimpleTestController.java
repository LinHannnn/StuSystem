package com.example.edumanage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleTestController {
    
    @GetMapping("/simple-test")
    public String simpleTest() {
        return "API is working";
    }
    
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
} 