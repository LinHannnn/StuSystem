package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.dto.CourseDTO;
import com.example.edumanage.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * 创建课程（需要ADMIN权限）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<CourseDTO>> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(createdCourse));
    }

    /**
     * 更新课程（需要ADMIN权限）
     * 只更新提供的字段，未提供的字段保持不变
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<CourseDTO>> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(Result.success(updatedCourse));
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<CourseDTO>> getCourse(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(Result.success(course));
    }

    /**
     * 删除课程（需要ADMIN权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Result.success());
    }

    /**
     * 分页获取课程列表
     */
    @GetMapping
    public ResponseEntity<Result<Page<CourseDTO>>> getCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<CourseDTO> courses = courseService.getCourses(pageable);
        return ResponseEntity.ok(Result.success(courses));
    }

    /**
     * 根据课程名称搜索课程
     */
    @GetMapping("/search")
    public ResponseEntity<Result<List<CourseDTO>>> searchCoursesByName(@RequestParam String name) {
        List<CourseDTO> courses = courseService.searchCoursesByName(name);
        return ResponseEntity.ok(Result.success(courses));
    }
    
    /**
     * 高级搜索课程 - 支持多条件组合查询
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<Result<List<CourseDTO>>> advancedSearchCourses(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Integer weekDay) {
            
        List<CourseDTO> courses = courseService.advancedSearch(
            courseName, classId, teacherId, weekDay);
        return ResponseEntity.ok(Result.success(courses));
    }

    /**
     * 获取指定班级的所有课程
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<Result<List<CourseDTO>>> getCoursesByClassId(@PathVariable String classId) {
        List<CourseDTO> courses = courseService.getCoursesByClassId(classId);
        return ResponseEntity.ok(Result.success(courses));
    }

    /**
     * 根据教师名称获取课程
     */
    @GetMapping("/teacher/{teacherName}")
    public ResponseEntity<Result<List<CourseDTO>>> getCoursesByTeacherName(@PathVariable String teacherName) {
        List<CourseDTO> courses = courseService.getCoursesByTeacherName(teacherName);
        return ResponseEntity.ok(Result.success(courses));
    }

    /**
     * 获取指定班级和教师的所有课程
     */
    @GetMapping("/class/{classId}/teacher/{teacherId}")
    public ResponseEntity<Result<List<CourseDTO>>> getCoursesByClassIdAndTeacherId(
            @PathVariable String classId, @PathVariable Long teacherId) {
        List<CourseDTO> courses = courseService.getCoursesByClassIdAndTeacherId(classId, teacherId);
        return ResponseEntity.ok(Result.success(courses));
    }
    
    /**
     * 获取指定星期几的课程
     */
    @GetMapping("/weekDay/{weekDay}")
    public ResponseEntity<Result<List<CourseDTO>>> getCoursesByWeekDay(@PathVariable Integer weekDay) {
        List<CourseDTO> courses = courseService.getCoursesByWeekDay(weekDay);
        return ResponseEntity.ok(Result.success(courses));
    }

    /**
     * 根据时间范围获取课程
     */
    @GetMapping("/time")
    public ResponseEntity<Result<List<CourseDTO>>> getCoursesByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime) {
        
        List<CourseDTO> courses = courseService.getCoursesByTimeRange(startTime, endTime);
        return ResponseEntity.ok(Result.success(courses));
    }
} 