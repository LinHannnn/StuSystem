package com.example.edumanage.service;

import com.example.edumanage.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

// 删除重复的Service实现类，保留接口定义
public interface StudentService {
    // 创建学员
    StudentDTO createStudent(StudentDTO studentDTO);
    
    // 更新学员信息
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    
    // 根据ID查询学员
    StudentDTO getStudentById(Long id);
    
    // 根据学号查询学员
    StudentDTO getStudentByStudentId(String studentId);
    
    // 根据条件查询学员
    List<StudentDTO> getStudentsByCondition(String keyword, String classId);
    
    // 根据学生姓名查询学员
    List<StudentDTO> getStudentsByName(String studentName);
    
    // 删除学员
    void deleteStudent(Long id);
    
    // 批量删除学员
    void deleteStudents(List<Long> ids);
    
    List<StudentDTO> getAllStudents();
}