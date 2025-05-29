package com.example.edumanage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadConfig.class);
    
    @Value("${app.upload.dir:/tmp/uploads/avatars}")
    private String uploadPath;
    
    @Bean
    public Boolean ensureUploadDirectoryExists() {
        try {
            Path path = Paths.get(uploadPath);
            File directory = path.toFile();
            
            logger.info("检查文件上传目录: {}", directory.getAbsolutePath());
            
            if (!directory.exists()) {
                try {
                    if (directory.mkdirs()) {
                        logger.info("成功创建文件上传目录: {}", directory.getAbsolutePath());
                    } else {
                        logger.error("无法创建文件上传目录: {}", directory.getAbsolutePath());
                        return false;
                    }
                } catch (Exception e) {
                    logger.error("创建目录时发生错误: {}", e.getMessage(), e);
                    
                    // 尝试使用Java NIO创建目录
                    try {
                        Files.createDirectories(path);
                        logger.info("通过Files.createDirectories成功创建目录: {}", path);
                    } catch (IOException ex) {
                        logger.error("通过Files.createDirectories创建目录失败: {}", ex.getMessage(), ex);
                        return false;
                    }
                }
            } else {
                logger.info("文件上传目录已存在: {}", directory.getAbsolutePath());
                
                // 检查目录权限
                if (!directory.canWrite()) {
                    logger.warn("文件上传目录不可写: {}", directory.getAbsolutePath());
                    try {
                        if(directory.setWritable(true)) {
                            logger.info("成功为文件上传目录设置写权限");
                        } else {
                            logger.warn("无法为文件上传目录设置写权限");
                            return false;
                        }
                    } catch (Exception e) {
                        logger.error("设置目录写权限时出错: {}", e.getMessage(), e);
                        return false;
                    }
                }
            }
            
            // 尝试在目录中创建和删除一个临时文件以验证权限
            try {
                File tempFile = new File(directory, "temp-test-file.txt");
                if (tempFile.createNewFile()) {
                    logger.info("成功创建测试文件: {}", tempFile.getAbsolutePath());
                    if (tempFile.delete()) {
                        logger.info("成功删除测试文件");
                    } else {
                        logger.warn("无法删除测试文件: {}", tempFile.getAbsolutePath());
                        return false;
                    }
                } else {
                    logger.warn("无法创建测试文件: {}", tempFile.getAbsolutePath());
                    return false;
                }
            } catch (Exception e) {
                logger.error("测试文件权限时出错: {}", e.getMessage(), e);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.error("初始化文件上传目录时出错: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Bean
    public FileSystemResource uploadDirectory() {
        return new FileSystemResource(uploadPath);
    }
} 