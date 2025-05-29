package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.dto.LoginRequest;
import com.example.edumanage.dto.LoginResponse;
import com.example.edumanage.dto.RegisterRequest;
import com.example.edumanage.dto.RegisterResponse;
import com.example.edumanage.model.User;
import com.example.edumanage.security.JwtTokenUtil;
import com.example.edumanage.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@Tag(name = "认证管理", description = "认证相关接口")
@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            User user = userService.findByUsername(userDetails.getUsername());

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .avatar(user.getAvatar())
                    .build();

            return Result.success(response);
        } catch (Exception e) {
            return Result.fail("登录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);
            
            RegisterResponse response = RegisterResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .fullName(user.getFullName())
                    .avatar(user.getAvatar())
                    .build();

            return Result.success(response);
        } catch (Exception e) {
            return Result.fail("注册失败：" + e.getMessage());
        }
    }
}