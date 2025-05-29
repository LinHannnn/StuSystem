CREATE TABLE `scores` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id` BIGINT(20) NOT NULL COMMENT '学生ID（关联students.id）',
  `course_id` BIGINT(20) NOT NULL COMMENT '课程ID（关联courses.id）',
  `exam_type` VARCHAR(50) NOT NULL DEFAULT 'FINAL' COMMENT '考试类型（FINAL:期末, MID:期中, QUIZ:测验）',
  `score` DECIMAL(5,2) NOT NULL COMMENT '成绩（0-100分，支持小数）',
  `semester` VARCHAR(20) NOT NULL COMMENT '学期（如：2025-SPRING）',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态（1正常 0删除）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_student_course_exam` (`student_id`, `course_id`, `exam_type`, `semester`),
  INDEX `idx_student_id` (`student_id`),
  INDEX `idx_course_id` (`course_id`),
  INDEX `idx_semester` (`semester`),
  
  CONSTRAINT `fk_scores_student` 
    FOREIGN KEY (`student_id`) 
    REFERENCES `students` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT `fk_scores_course` 
    FOREIGN KEY (`course_id`) 
    REFERENCES `courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表'; 