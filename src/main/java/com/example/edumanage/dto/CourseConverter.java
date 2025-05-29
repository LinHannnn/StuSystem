package com.example.edumanage.dto;

import com.example.edumanage.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseConverter {
    
    public CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }
        
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setWeekDay(course.getWeekDay());
        dto.setStartTime(course.getStartTime());
        dto.setEndTime(course.getEndTime());
        
        if (course.getClassInfo() != null) {
            dto.setClassId(course.getClassInfo().getClassId());
            dto.setClassName(course.getClassInfo().getClassName());
            dto.setClassroom(course.getClassInfo().getClassroom());
        }
        
        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
            dto.setTeacherName(course.getTeacher().getName());
        }
        
        return dto;
    }
    
    public Course toEntity(CourseDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Course course = new Course();
        
        // 不设置id，让数据库自动生成
        course.setCourseName(dto.getCourseName());
        course.setWeekDay(dto.getWeekDay());
        course.setStartTime(dto.getStartTime());
        course.setEndTime(dto.getEndTime());
        
        return course;
    }
    
    public void updateEntityFromDTO(CourseDTO dto, Course course) {
        if (dto == null || course == null) {
            return;
        }
        
        if (dto.getCourseName() != null) {
            course.setCourseName(dto.getCourseName());
        }
        
        if (dto.getWeekDay() != null) {
            course.setWeekDay(dto.getWeekDay());
        }
        
        if (dto.getStartTime() != null) {
            course.setStartTime(dto.getStartTime());
        }
        
        if (dto.getEndTime() != null) {
            course.setEndTime(dto.getEndTime());
        }
    }
} 