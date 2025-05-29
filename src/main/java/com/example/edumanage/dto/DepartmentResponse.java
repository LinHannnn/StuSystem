package com.example.edumanage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DepartmentResponse {
    private Long id;
    private String departmentName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 