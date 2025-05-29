package com.example.edumanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test/db")
    public String testConnection() {
        try {
            // 测试数据库连接
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "数据库连接成功！";
        } catch (Exception e) {
            return "数据库连接失败：" + e.getMessage();
        }
    }
} 