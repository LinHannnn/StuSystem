package com.example.edumanage.dto;

import com.example.edumanage.model.Student;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentConverter {
    
    public StudentDTO toDTO(Student student) {
        if (student == null) {
            return null;
        }
        
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setStudentId(student.getStudentId());
        dto.setStudentName(student.getStudentName());
        dto.setGender(student.getGender());
        dto.setAge(student.getAge());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setIdCard(student.getIdCard());
        dto.setAddress(student.getAddress());
        dto.setEducation(student.getEducation());
        dto.setGraduationDate(student.getGraduationDate());
        dto.setClassId(student.getClassId());
        dto.setStatus(student.getStatus());
        dto.setCreateTime(student.getCreateTime());
        dto.setUpdateTime(student.getUpdateTime());
        
        return dto;
    }
    
    public Student toEntity(StudentDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Student student = new Student();
        student.setId(dto.getId());
        student.setStudentId(dto.getStudentId());
        student.setStudentName(dto.getStudentName());
        student.setGender(dto.getGender());
        student.setAge(dto.getAge());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setIdCard(dto.getIdCard());
        student.setAddress(dto.getAddress());
        student.setEducation(dto.getEducation());
        student.setGraduationDate(dto.getGraduationDate());
        student.setClassId(dto.getClassId());
        student.setStatus(dto.getStatus());
        student.setCreateTime(dto.getCreateTime());
        student.setUpdateTime(dto.getUpdateTime());
        
        return student;
    }
    
    public List<StudentDTO> toDTOList(List<Student> students) {
        return students.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 