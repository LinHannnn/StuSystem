package com.example.edumanage.dto;

import com.example.edumanage.model.Employee.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于员工部分更新的DTO，所有字段都是可选的
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeUpdateDTO {
    private Long id;
    
    private String username;
    
    private String name;
    
    private Gender gender;
    
    @Min(value = 18, message = "年龄必须大于或等于18岁")
    @Max(value = 100, message = "年龄必须小于或等于100岁")
    private Integer age;
    
    private Long departmentId;
    
    private String position;
    
    private BigDecimal salary;
    
    private LocalDate hireDate;
    
    @Pattern(regexp = "^data:image/(jpeg|png|gif|jpg|webp|svg\\+xml);base64,[A-Za-z0-9+/=]+$", message = "头像必须是base64格式的图片(支持jpeg/png/gif/jpg/webp/svg格式)")
    private String avatar;
    
    @Valid
    private List<WorkExperienceDTO> workExperiences;
} 