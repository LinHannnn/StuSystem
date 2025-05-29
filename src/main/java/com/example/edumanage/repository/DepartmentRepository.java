package com.example.edumanage.repository;

import com.example.edumanage.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentName(String departmentName);
    boolean existsByDepartmentNameAndIdNot(String departmentName, Long id);
    
    /**
     * 根据部门名称模糊查询
     */
    List<Department> findByDepartmentNameContaining(String departmentName);
} 