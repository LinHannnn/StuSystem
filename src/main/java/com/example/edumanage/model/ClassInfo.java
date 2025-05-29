package com.example.edumanage.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "classes")
public class ClassInfo {
    @Id
    @Column(name = "class_id")
    private String classId;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "classroom")
    private String classroom;     // 班级教室

    @Column(name = "grade", nullable = false)
    private String grade;    // 年级

    @Column(name = "start_date")
    private LocalDate startDate;  // 开课时间

    @Column(name = "end_date")
    private LocalDate endDate;    // 结课时间

    @Column(name = "status")
    private String status;   // 班级状态：PENDING, ACTIVE, FINISHED, CANCELLED

    @Column(name = "head_teacher_id")
    private Long headTeacherId;

    @Column(name = "description")
    private String description;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
} 