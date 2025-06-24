package com.example.edumanage.service.impl;

import com.example.edumanage.model.User;
import com.example.edumanage.model.UserRole;
import com.example.edumanage.repository.UserRepository;
import com.example.edumanage.service.UserService;
import com.example.edumanage.dto.RegisterRequest;
import com.example.edumanage.dto.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 为了测试方便，直接使用明文密码
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),  // 直接使用存储的密码，不论是否加密
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    @Override
    public User registerUser(RegisterRequest registerRequest) {
        // 验证用户名是否已存在
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 验证邮箱是否已被使用
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已被使用");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        
        // 设置用户全名
        if (registerRequest.getFullName() != null && !registerRequest.getFullName().isEmpty()) {
            user.setFullName(registerRequest.getFullName());
        }
        
        // 设置用户角色，如果请求中没有指定则默认为STUDENT
        UserRole role = UserRole.STUDENT; // 默认为学生用户
        if (registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()) {
            try {
                role = UserRole.valueOf(registerRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // 如果提供的角色无效，使用默认角色
                System.out.println("无效的角色: " + registerRequest.getRole() + "，使用默认角色STUDENT");
            }
        }
        user.setRole(role);

        // 处理头像
        if (registerRequest.getAvatar() != null && !registerRequest.getAvatar().isEmpty()) {
            try {
                // 验证base64格式
                String base64Content = registerRequest.getAvatar();
                
                // 检查格式是否正确
                if (!base64Content.startsWith("data:image/")) {
                    throw new RuntimeException("头像格式不正确，必须是base64编码的图片");
                }
                
                // 获取图片类型
                String[] parts = base64Content.split(",");
                if (parts.length != 2) {
                    throw new RuntimeException("头像格式不正确，base64编码格式有误");
                }
                
                // 解码并检查大小
                String base64Image = parts[1];
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
                
                // 限制头像大小为2MB
                if (imageBytes.length > 2 * 1024 * 1024) {
                    throw new RuntimeException("头像图片不能超过2MB");
                }
                
                // 保存头像
                user.setAvatar(base64Content);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("头像base64编码无效: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("头像处理失败: " + e.getMessage());
            }
        }

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }

    @Override
    @Transactional
    public User updateUser(String username, UpdateUserRequest updateUserRequest) {
        User user = findByUsername(username);
        
        // 更新用户名
        if (updateUserRequest.getUsername() != null && 
            !updateUserRequest.getUsername().equals(user.getUsername())) {
            // 检查新用户名是否已存在
            if (userRepository.findByUsername(updateUserRequest.getUsername()).isPresent()) {
                throw new RuntimeException("新用户名已被使用");
            }
            user.setUsername(updateUserRequest.getUsername());
        }

        // 更新邮箱
        if (updateUserRequest.getEmail() != null && 
            !updateUserRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updateUserRequest.getEmail()).isPresent()) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(updateUserRequest.getEmail());
        }

        // 更新姓名
        if (updateUserRequest.getFullName() != null) {
            user.setFullName(updateUserRequest.getFullName());
        }
        
        // 更新手机号
        if (updateUserRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }

        // 更新头像
        if (updateUserRequest.getAvatar() != null) {
            try {
                // 验证base64格式
                String base64Content = updateUserRequest.getAvatar();
                
                // 检查格式是否正确
                if (!base64Content.startsWith("data:image/")) {
                    throw new RuntimeException("头像格式不正确，必须是base64编码的图片");
                }
                
                // 获取图片类型
                String[] parts = base64Content.split(",");
                if (parts.length != 2) {
                    throw new RuntimeException("头像格式不正确，base64编码格式有误");
                }
                
                // 解码并检查大小
                String base64Image = parts[1];
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
                
                // 限制头像大小为2MB
                if (imageBytes.length > 2 * 1024 * 1024) {
                    throw new RuntimeException("头像图片不能超过2MB");
                }
                
                // 保存头像
                user.setAvatar(base64Content);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("头像base64编码无效: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("头像处理失败: " + e.getMessage());
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        
        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String username, String newPassword) {
        User user = findByUsername(username);
        
        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
} 