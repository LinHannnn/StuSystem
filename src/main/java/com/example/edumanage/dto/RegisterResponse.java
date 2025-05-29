package com.example.edumanage.dto;

import com.example.edumanage.model.UserRole;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RegisterResponse {
    private String username;
    private String email;
    private UserRole role;
    private String fullName;
    private String avatar;
} 