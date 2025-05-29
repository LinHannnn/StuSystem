package com.example.edumanage.controller;

import com.example.edumanage.dto.ClassDTO;
import com.example.edumanage.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClasses() {
        List<ClassDTO> classes = classService.getAllClasses();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        response.put("data", classes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<Map<String, Object>> getClassById(@PathVariable String classId) {
        ClassDTO classDTO = classService.getClassById(classId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        response.put("data", classDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createClass(@RequestBody ClassDTO classDTO) {
        ClassDTO createdClass = classService.createClass(classDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        response.put("data", createdClass);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{classId}")
    public ResponseEntity<Map<String, Object>> updateClass(@PathVariable String classId, @RequestBody ClassDTO classDTO) {
        ClassDTO updatedClass = classService.updateClass(classId, classDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        response.put("data", updatedClass);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteClass(@RequestBody Map<String, String> requestBody) {
        String classId = requestBody.get("classId");
        if (classId == null || classId.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 0);
            errorResponse.put("message", "classId不能为空");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
        classService.deleteClass(classId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 0);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getClassesByStatus(@PathVariable String status) {
        List<ClassDTO> classes = classService.getClassesByStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        response.put("data", classes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dateRange")
    public ResponseEntity<Map<String, Object>> getClassesByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<ClassDTO> classes = classService.getClassesByDateRange(startDate, endDate);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        response.put("data", classes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{className}")
    public ResponseEntity<Map<String, Object>> getClassByClassName(@PathVariable String className) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 先尝试精确查询
            ClassDTO classDTO = classService.getClassByClassName(className);
            response.put("code", 1);
            response.put("message", "success");
            response.put("data", classDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 精确查询失败，尝试模糊查询
            List<ClassDTO> classes = classService.getClassesByClassNameContaining(className);
            
            if (!classes.isEmpty()) {
                // 模糊查询有结果
                response.put("code", 1);
                response.put("message", "success");
                response.put("data", classes);
                return ResponseEntity.ok(response);
            } else {
                // 如果模糊查询也没有结果，返回原始错误
                response.put("code", 0);
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }
    }
    
    /**
     * 搜索班级 - 支持多条件组合查询和模糊匹配
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchClasses(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String classroom,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long headTeacherId) {
        
        List<ClassDTO> classes;
        
        // 检查是否只提供了单个条件
        if (className != null && grade == null && classroom == null && status == null && headTeacherId == null) {
            // 仅按班级名称模糊查询
            classes = classService.getClassesByClassNameContaining(className);
        } else if (grade != null && className == null && classroom == null && status == null && headTeacherId == null) {
            // 仅按年级模糊查询
            classes = classService.getClassesByGradeContaining(grade);
        } else if (classroom != null && className == null && grade == null && status == null && headTeacherId == null) {
            // 仅按教室模糊查询
            classes = classService.getClassesByClassroomContaining(classroom);
        } else if (headTeacherId != null && className == null && grade == null && classroom == null && status == null) {
            // 仅按班主任ID查询
            classes = classService.getClassesByHeadTeacherId(headTeacherId);
        } else if (status != null && className == null && grade == null && classroom == null && headTeacherId == null) {
            // 仅按状态查询
            classes = classService.getClassesByStatus(status);
        } else if (className != null || grade != null || classroom != null || status != null || headTeacherId != null) {
            // 多条件组合查询 - 模糊匹配
            classes = classService.advancedSearch(className, grade, classroom, status, headTeacherId);
        } else {
            // 没有搜索条件，返回所有班级
            classes = classService.getAllClasses();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 1);
        response.put("message", "success");
        response.put("data", classes);
        return ResponseEntity.ok(response);
    }
} 