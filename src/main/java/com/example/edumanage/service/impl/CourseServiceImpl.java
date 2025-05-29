package com.example.edumanage.service.impl;

import com.example.edumanage.dto.CourseConverter;
import com.example.edumanage.dto.CourseDTO;
import com.example.edumanage.exception.ResourceNotFoundException;
import com.example.edumanage.model.ClassInfo;
import com.example.edumanage.model.Course;
import com.example.edumanage.model.Employee;
import com.example.edumanage.repository.ClassRepository;
import com.example.edumanage.repository.CourseRepository;
import com.example.edumanage.repository.EmployeeRepository;
import com.example.edumanage.service.CourseService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ClassRepository classRepository;
    private final EmployeeRepository employeeRepository;
    private final CourseConverter courseConverter;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, 
                            ClassRepository classRepository, 
                            EmployeeRepository employeeRepository, 
                            CourseConverter courseConverter) {
        this.courseRepository = courseRepository;
        this.classRepository = classRepository;
        this.employeeRepository = employeeRepository;
        this.courseConverter = courseConverter;
    }

    @Override
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = courseConverter.toEntity(courseDTO);
        
        // 设置班级
        if (courseDTO.getClassId() != null) {
            ClassInfo classInfo = classRepository.findById(courseDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("班级不存在: " + courseDTO.getClassId()));
            course.setClassInfo(classInfo);
        }
        
        // 设置教师（必填）
        if (courseDTO.getTeacherId() == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        
        Employee teacher = employeeRepository.findById(courseDTO.getTeacherId())
            .orElseThrow(() -> new ResourceNotFoundException("教师不存在: " + courseDTO.getTeacherId()));
        course.setTeacher(teacher);
        
        Course savedCourse = courseRepository.save(course);
        return courseConverter.toDTO(savedCourse);
    }

    @Override
    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("课程不存在: " + id));
        
        // 更新课程基本信息（只更新非空字段）
        courseConverter.updateEntityFromDTO(courseDTO, existingCourse);
        
        // 更新班级关联
        if (courseDTO.getClassId() != null) {
            ClassInfo classInfo = classRepository.findById(courseDTO.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("班级不存在: " + courseDTO.getClassId()));
            existingCourse.setClassInfo(classInfo);
        }
        
        // 更新教师关联
        if (courseDTO.getTeacherId() != null) {
            Employee teacher = employeeRepository.findById(courseDTO.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("教师不存在: " + courseDTO.getTeacherId()));
            existingCourse.setTeacher(teacher);
        }
        
        Course updatedCourse = courseRepository.save(existingCourse);
        return courseConverter.toDTO(updatedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("课程不存在: " + id));
        return courseConverter.toDTO(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("课程不存在: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
            .map(courseConverter::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> searchCoursesByName(String courseName) {
        return courseRepository.findByCourseNameContaining(courseName).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByClassId(String classId) {
        return courseRepository.findByClassInfoClassId(classId).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByTeacherName(String teacherName) {
        return courseRepository.findByTeacherName(teacherName).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByClassIdAndTeacherId(String classId, Long teacherId) {
        return courseRepository.findByClassInfoClassIdAndTeacherId(classId, teacherId).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByWeekDay(Integer weekDay) {
        return courseRepository.findByWeekDay(weekDay).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByTimeRange(LocalTime startTime, LocalTime endTime) {
        return courseRepository.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(endTime, startTime).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> advancedSearch(String courseName, String classId, Long teacherId, Integer weekDay) {
        Specification<Course> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (courseName != null && !courseName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("courseName"), "%" + courseName + "%"));
            }
            
            if (classId != null && !classId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("classInfo").get("classId"), classId));
            }
            
            if (teacherId != null) {
                predicates.add(criteriaBuilder.equal(root.get("teacher").get("id"), teacherId));
            }
            
            if (weekDay != null) {
                predicates.add(criteriaBuilder.equal(root.get("weekDay"), weekDay));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return courseRepository.findAll(spec).stream()
            .map(courseConverter::toDTO)
            .collect(Collectors.toList());
    }
} 