package com.example.edumanage.service;

import com.example.edumanage.dto.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;

public interface CourseService {
    /**
     * 创建课程
     */
    CourseDTO createCourse(CourseDTO courseDTO);
    
    /**
     * 更新课程
     */
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    
    /**
     * 获取课程详情
     */
    CourseDTO getCourseById(Long id);
    
    /**
     * 删除课程
     */
    void deleteCourse(Long id);
    
    /**
     * 分页获取课程列表
     */
    Page<CourseDTO> getCourses(Pageable pageable);
    
    /**
     * 根据课程名称搜索课程
     */
    List<CourseDTO> searchCoursesByName(String courseName);
    
    /**
     * 获取指定班级的所有课程
     */
    List<CourseDTO> getCoursesByClassId(String classId);
    
    /**
     * 获取指定教师的所有课程
     */
    List<CourseDTO> getCoursesByTeacherId(Long teacherId);
    
    /**
     * 根据教师名称获取课程
     */
    List<CourseDTO> getCoursesByTeacherName(String teacherName);
    
    /**
     * 获取指定班级和教师的所有课程
     */
    List<CourseDTO> getCoursesByClassIdAndTeacherId(String classId, Long teacherId);
    
    /**
     * 根据星期几获取课程
     */
    List<CourseDTO> getCoursesByWeekDay(Integer weekDay);
    
    /**
     * 根据课程时间段获取课程
     */
    List<CourseDTO> getCoursesByTimeRange(LocalTime startTime, LocalTime endTime);
    
    /**
     * 高级搜索 - 支持多条件组合查询
     */
    List<CourseDTO> advancedSearch(String courseName, String classId, Long teacherId, Integer weekDay);
} 