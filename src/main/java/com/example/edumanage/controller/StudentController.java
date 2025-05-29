package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.dto.StudentDTO;
import com.example.edumanage.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping
    public Result<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return Result.success(createdStudent);
    }

    @PutMapping("/{id}")
    public Result<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return Result.success(updatedStudent);
    }

    @GetMapping("/{id}")
    public Result<StudentDTO> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return Result.success(student);
    }

    @GetMapping("/search")
    public Result<List<StudentDTO>> searchStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String classId) {
        List<StudentDTO> students = studentService.getStudentsByCondition(keyword, classId);
        return Result.success(students);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteStudents(@RequestBody List<Long> ids) {
        studentService.deleteStudents(ids);
        return Result.success();
    }

    @GetMapping("/by-student-id/{studentId}")
    public Result<StudentDTO> getStudentByStudentId(@PathVariable String studentId) {
        StudentDTO student = studentService.getStudentByStudentId(studentId);
        return Result.success(student);
    }

    @GetMapping("/by-name/{studentName}")
    public Result<List<StudentDTO>> getStudentsByName(@PathVariable String studentName) {
        List<StudentDTO> students = studentService.getStudentsByName(studentName);
        return Result.success(students);
    }

    @GetMapping
    public Result<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return Result.success(students);
    }
}