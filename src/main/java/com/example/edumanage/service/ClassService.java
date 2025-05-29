package com.example.edumanage.service;

import com.example.edumanage.dto.ClassDTO;
import java.time.LocalDate;
import java.util.List;

public interface ClassService {
    List<ClassDTO> getAllClasses();
    ClassDTO getClassById(String classId);
    ClassDTO createClass(ClassDTO classDTO);
    ClassDTO updateClass(String classId, ClassDTO classDTO);
    void deleteClass(String classId);
    List<ClassDTO> getClassesByStatus(String status);
    List<ClassDTO> getClassesByDateRange(LocalDate startDate, LocalDate endDate);
    ClassDTO getClassByClassName(String className);
    
    /**
     * 根据班级名称模糊查询
     */
    List<ClassDTO> getClassesByClassNameContaining(String className);
    
    /**
     * 根据年级模糊查询
     */
    List<ClassDTO> getClassesByGradeContaining(String grade);
    
    /**
     * 根据教室模糊查询
     */
    List<ClassDTO> getClassesByClassroomContaining(String classroom);
    
    /**
     * 根据班主任ID查询
     */
    List<ClassDTO> getClassesByHeadTeacherId(Long headTeacherId);
    
    /**
     * 高级搜索 - 支持多条件组合查询
     */
    List<ClassDTO> advancedSearch(String className, String grade, String classroom, String status, Long headTeacherId);
} 