package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/filetest")
public class FileTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileTestController.class);

    @Value("${app.upload.dir:/tmp/uploads/avatars}")
    private String uploadDir;
    
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/check")
    public Result<Map<String, Object>> checkUploadDir() {
        Map<String, Object> result = new HashMap<>();
        logger.info("开始检查上传目录: {}", uploadDir);
        
        try {
            // 获取基本系统信息
            result.put("uploadDir", uploadDir);
            result.put("javaVersion", System.getProperty("java.version"));
            result.put("osName", System.getProperty("os.name"));
            result.put("osVersion", System.getProperty("os.version"));
            result.put("userHome", System.getProperty("user.home"));
            result.put("userDir", System.getProperty("user.dir"));
            
            // 获取文件存储服务的状态
            boolean storageReady = fileStorageService.isStorageReady();
            result.put("storageReady", storageReady);
            
            if (storageReady) {
                Path uploadPath = fileStorageService.getFileStorageLocation();
                String absolutePath = uploadPath.toString();
                result.put("absolutePath", absolutePath);
                
                // 检查目录是否存在
                boolean exists = Files.exists(uploadPath);
                result.put("exists", exists);
                
                // 检查是否可写
                if (exists) {
                    boolean canWrite = Files.isWritable(uploadPath);
                    result.put("canWrite", canWrite);
                }
            }
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("检查上传目录时出现错误: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getName());
            return Result.fail("检查上传目录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/healthcheck")
    public Result<String> healthCheck() {
        return Result.success("File test controller is running");
    }
} 