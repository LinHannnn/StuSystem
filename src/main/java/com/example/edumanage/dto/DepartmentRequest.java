package com.example.edumanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank(message = "部门名称不能为空")
    private String departmentName;
} 