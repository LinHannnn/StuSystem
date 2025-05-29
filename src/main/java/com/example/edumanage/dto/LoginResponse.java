package com.example.edumanage.dto;

import com.example.edumanage.model.UserRole;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private UserRole role;
    private String email;
    private String fullName;
    private String avatar;
} 