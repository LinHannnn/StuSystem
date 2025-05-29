package com.example.edumanage.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;

@Data
public class StudentRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别值无效")
    @Max(value = 1, message = "性别值无效")
    private Integer gender;
    
    @NotBlank(message = "学号不能为空")
    private String studentId;
    
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    
    private String idCard;
    private String address;
    private String highestDegree;
    private LocalDate graduationDate;
    
    @NotBlank(message = "班级ID不能为空")
    private String classId;
}