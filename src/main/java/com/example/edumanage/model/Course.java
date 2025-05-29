package com.example.edumanage.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassInfo classInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Employee teacher;
    
    @Column(name = "week_day", nullable = false)
    private Integer weekDay;  // 星期几(1-7)
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;  // 上课时间
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;  // 下课时间
} 