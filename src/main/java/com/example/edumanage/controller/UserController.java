package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.dto.ResetPasswordRequest;
import com.example.edumanage.dto.UpdateUserRequest;
import com.example.edumanage.model.User;
import com.example.edumanage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public Result<User> updateUser(Authentication authentication, @Valid @RequestBody UpdateUserRequest request) {
        try {
            User updatedUser = userService.updateUser(authentication.getName(), request);
            return Result.success(updatedUser);
        } catch (Exception e) {
            return Result.fail("更新用户信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(Authentication authentication, @Valid @RequestBody ResetPasswordRequest request) {
        try {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return Result.fail("两次输入的密码不一致");
            }
            userService.resetPassword(authentication.getName(), request.getOldPassword(), request.getNewPassword());
            return Result.success();
        } catch (Exception e) {
            return Result.fail("重置密码失败：" + e.getMessage());
        }
    }
} 