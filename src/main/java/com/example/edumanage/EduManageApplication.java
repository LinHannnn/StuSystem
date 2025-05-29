package com.example.edumanage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class EduManageApplication extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(EduManageApplication.class);

    public static void main(String[] args) {
        logger.info("启动教学管理系统...");
        ApplicationContext context = SpringApplication.run(EduManageApplication.class, args);
        logger.info("教学管理系统启动成功");
    }
}