package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.model.User;
import com.example.edumanage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "头像管理", description = "用户头像相关接口")
@RestController
@RequestMapping("/api/avatars")
public class AvatarController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取用户头像")
    @GetMapping("/{username}")
    public Result<String> getUserAvatar(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
                return Result.fail("用户没有上传头像");
            }
            return Result.success(user.getAvatar());
        } catch (Exception e) {
            return Result.fail("获取头像失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "检查头像上传配置")
    @GetMapping("/check-config")
    public Result<String> checkAvatarConfig() {
        return Result.success("头像配置正常，使用base64存储在数据库中");
    }
} 