package com.example.edumanage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    @Value("${app.upload.dir:/tmp/uploads/avatars}")
    private String uploadDir;
    
    private Path fileStorageLocation;
    
    @PostConstruct
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            logger.info("初始化文件存储位置: {}", fileStorageLocation);
            
            if (!Files.exists(fileStorageLocation)) {
                try {
                    Files.createDirectories(fileStorageLocation);
                    logger.info("创建文件上传目录: {}", fileStorageLocation);
                } catch (IOException ex) {
                    logger.error("无法创建文件上传目录: {}", fileStorageLocation, ex);
                }
            } else {
                logger.info("文件上传目录已存在: {}", fileStorageLocation);
            }
            
            // 检查目录权限
            if (!Files.isWritable(fileStorageLocation)) {
                logger.warn("文件上传目录不可写: {}", fileStorageLocation);
            } else {
                logger.info("文件上传目录可写: {}", fileStorageLocation);
            }
        } catch (Exception ex) {
            logger.error("初始化文件存储服务失败", ex);
        }
    }
    
    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
    
    public boolean isStorageReady() {
        return Files.exists(fileStorageLocation) && Files.isWritable(fileStorageLocation);
    }
} 