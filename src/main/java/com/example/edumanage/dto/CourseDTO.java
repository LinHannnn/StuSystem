package com.example.edumanage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalTime;

@Data
public class CourseDTO {
    private Long id;
    private String courseName;
    private String classId;
    private String className;
    private String classroom;  // 班级教室
    private Long teacherId;
    private String teacherName;
    private Integer weekDay;  // 星期几(1-7)
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;  // 上课时间
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;  // 下课时间
} 