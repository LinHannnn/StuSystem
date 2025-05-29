package com.example.edumanage.service;

import com.example.edumanage.model.User;
import com.example.edumanage.dto.RegisterRequest;
import com.example.edumanage.dto.UpdateUserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerUser(RegisterRequest registerRequest);
    User findByUsername(String username);
    User updateUser(String username, UpdateUserRequest updateUserRequest);
    void resetPassword(String username, String oldPassword, String newPassword);
} 