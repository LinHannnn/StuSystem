package com.example.edumanage.service.impl;

import com.example.edumanage.dto.StudentConverter;
import com.example.edumanage.dto.StudentDTO;
import com.example.edumanage.model.Student;
import com.example.edumanage.exception.ResourceNotFoundException;
import com.example.edumanage.repository.StudentRepository;
import com.example.edumanage.service.StudentService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentConverter studentConverter;

    @Override
    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = studentConverter.toEntity(studentDTO);
        student = studentRepository.save(student);
        return studentConverter.toDTO(student);
    }

    @Override
    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        // 手动更新实体属性
        student.setStudentName(studentDTO.getStudentName());
        student.setGender(studentDTO.getGender());
        student.setStudentId(studentDTO.getStudentId());
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setIdCard(studentDTO.getIdCard());
        student.setAddress(studentDTO.getAddress());
        student.setEducation(studentDTO.getEducation());
        student.setGraduationDate(studentDTO.getGraduationDate());
        student.setClassId(studentDTO.getClassId());
        student.setStatus(studentDTO.getStatus());
        
        student = studentRepository.save(student);
        return studentConverter.toDTO(student);
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentConverter.toDTO(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteStudents(List<Long> ids) {
        studentRepository.deleteAllById(ids);
    }

    @Override
    public StudentDTO getStudentByStudentId(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with studentId: " + studentId));
        return studentConverter.toDTO(student);
    }

    @Override
    public List<StudentDTO> getStudentsByCondition(String keyword, String classId) {
        Specification<Student> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.or(
                    cb.like(root.get("studentName"), "%" + keyword + "%"),
                    cb.like(root.get("studentId"), "%" + keyword + "%"),
                    cb.like(root.get("phoneNumber"), "%" + keyword + "%")
                ));
            }
            
            if (StringUtils.hasText(classId)) {
                predicates.add(cb.equal(root.get("classId"), classId));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        List<Student> students = studentRepository.findAll(spec);
        return students.stream()
                .map(studentConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(studentConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsByName(String studentName) {
        List<Student> students = studentRepository.findByStudentNameContaining(studentName);
        return students.stream()
                .map(studentConverter::toDTO)
                .collect(Collectors.toList());
    }
} 