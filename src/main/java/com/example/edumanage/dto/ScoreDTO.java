package com.example.edumanage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScoreDTO {
    private Long id;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    // 用于显示学生信息
    private String studentName;
    private String studentCode;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    // 用于显示课程信息
    private String courseName;

    @NotBlank(message = "考试类型不能为空")
    @Pattern(regexp = "^(FINAL|MID|QUIZ)$", message = "考试类型必须是FINAL、MID或QUIZ")
    private String examType;

    @NotNull(message = "成绩不能为空")
    @DecimalMin(value = "0.0", message = "成绩不能小于0")
    @DecimalMax(value = "100.0", message = "成绩不能大于100")
    private BigDecimal score;

    // 是否及格(0:及格, 1:不及格)
    private Integer passed;

    @NotBlank(message = "学期不能为空")
    @Pattern(regexp = "^\\d{4}-(SPRING|SUMMER|FALL|WINTER)$", message = "学期格式必须为yyyy-SEASON，如2025-SPRING")
    private String semester;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 