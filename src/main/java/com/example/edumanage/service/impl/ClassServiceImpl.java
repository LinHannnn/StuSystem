package com.example.edumanage.service.impl;

import com.example.edumanage.dto.ClassDTO;
import com.example.edumanage.model.ClassInfo;
import com.example.edumanage.repository.ClassRepository;
import com.example.edumanage.service.ClassService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Override
    public List<ClassDTO> getAllClasses() {
        return classRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClassDTO getClassById(String classId) {
        return classRepository.findById(classId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("班级不存在"));
    }

    @Override
    public ClassDTO createClass(ClassDTO classDTO) {
        ClassInfo classInfo = convertToEntity(classDTO);
        if (classInfo.getClassId() == null || classInfo.getClassId().trim().isEmpty()) {
            classInfo.setClassId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        }
        ClassInfo savedClass = classRepository.save(classInfo);
        return convertToDTO(savedClass);
    }

    @Override
    public ClassDTO updateClass(String classId, ClassDTO classDTO) {
        ClassInfo existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("班级不存在"));
        
        existingClass.setClassName(classDTO.getClassName());
        existingClass.setGrade(classDTO.getGrade());
        existingClass.setStartDate(classDTO.getStartDate());
        existingClass.setEndDate(classDTO.getEndDate());
        existingClass.setStatus(classDTO.getStatus());
        existingClass.setClassroom(classDTO.getClassroom());
        existingClass.setHeadTeacherId(classDTO.getHeadTeacherId());
        
        ClassInfo updatedClass = classRepository.save(existingClass);
        return convertToDTO(updatedClass);
    }

    @Override
    public void deleteClass(String classId) {
        if (!classRepository.existsById(classId)) {
            throw new RuntimeException("班级不存在");
        }
        
        try {
        classRepository.deleteById(classId);
        } catch (DataIntegrityViolationException e) {
            // 捕获数据完整性异常，通常是由于外键约束引起的
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            
            if (rootCause instanceof SQLException) {
                SQLException sqlEx = (SQLException) rootCause;
                // 检查是否是MySQL的1451错误码（外键约束失败）
                if (sqlEx.getErrorCode() == 1451) {
                    throw new RuntimeException("班级存在学生不能删除");
                }
            }
            
            // 如果不是特定的外键错误，则重新抛出原异常
            throw e;
        }
    }

    @Override
    public List<ClassDTO> getClassesByStatus(String status) {
        return classRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassDTO> getClassesByDateRange(LocalDate startDate, LocalDate endDate) {
        return classRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClassDTO getClassByClassName(String className) {
        ClassInfo classInfo = classRepository.findByClassName(className);
        if (classInfo == null) {
            throw new RuntimeException("未找到名为 " + className + " 的班级");
        }
        return convertToDTO(classInfo);
    }
    
    @Override
    public List<ClassDTO> getClassesByClassNameContaining(String className) {
        return classRepository.findByClassNameContaining(className).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClassDTO> getClassesByGradeContaining(String grade) {
        return classRepository.findByGradeContaining(grade).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClassDTO> getClassesByClassroomContaining(String classroom) {
        return classRepository.findByClassroomContaining(classroom).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClassDTO> getClassesByHeadTeacherId(Long headTeacherId) {
        return classRepository.findByHeadTeacherId(headTeacherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClassDTO> advancedSearch(String className, String grade, String classroom, String status, Long headTeacherId) {
        Specification<ClassInfo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (className != null && !className.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("className"), "%" + className + "%"));
            }
            
            if (grade != null && !grade.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("grade"), "%" + grade + "%"));
            }
            
            if (classroom != null && !classroom.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("classroom"), "%" + classroom + "%"));
            }
            
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            if (headTeacherId != null) {
                predicates.add(criteriaBuilder.equal(root.get("headTeacherId"), headTeacherId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return classRepository.findAll(spec).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ClassDTO convertToDTO(ClassInfo classEntity) {
        ClassDTO dto = new ClassDTO();
        dto.setClassId(classEntity.getClassId());
        dto.setClassName(classEntity.getClassName());
        dto.setGrade(classEntity.getGrade());
        dto.setStartDate(classEntity.getStartDate());
        dto.setEndDate(classEntity.getEndDate());
        dto.setStatus(classEntity.getStatus());
        dto.setClassroom(classEntity.getClassroom());
        dto.setHeadTeacherId(classEntity.getHeadTeacherId());
        return dto;
    }

    private ClassInfo convertToEntity(ClassDTO dto) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassId(dto.getClassId());
        classInfo.setClassName(dto.getClassName());
        classInfo.setGrade(dto.getGrade());
        classInfo.setStartDate(dto.getStartDate());
        classInfo.setEndDate(dto.getEndDate());
        classInfo.setStatus(dto.getStatus());
        classInfo.setClassroom(dto.getClassroom());
        classInfo.setHeadTeacherId(dto.getHeadTeacherId());
        return classInfo;
    }
} 