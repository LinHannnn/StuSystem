package com.example.edumanage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {
    private Long id;

    @NotBlank(message = "学员姓名不能为空")
    private String studentName;

    @NotNull(message = "性别不能为空")
    private Integer gender;  // 1代表男，0代表女
    
    @NotNull(message = "年龄不能为空")
    @Min(value = 6, message = "年龄必须大于或等于6岁")
    @Max(value = 100, message = "年龄必须小于或等于100岁")
    private Integer age;

    @NotBlank(message = "学号不能为空")
    private String studentId;  // 学号

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    private String phoneNumber;

    private String idCard;

    private String address;

    private String education;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    @NotBlank(message = "所属班级不能为空")
    private String classId;  // 班级ID

    private String className;  // 班级名称（仅用于响应）

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;
} 