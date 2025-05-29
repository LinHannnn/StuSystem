package com.example.edumanage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String fullName;

    @Pattern(regexp = "^data:image/(jpeg|png|gif|jpg|webp|svg\\+xml);base64,[A-Za-z0-9+/=]+$", message = "头像必须是base64格式的图片(支持jpeg/png/gif/jpg/webp/svg格式)")
    private String avatar;
} 