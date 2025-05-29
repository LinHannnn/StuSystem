package com.example.edumanage.repository;

import com.example.edumanage.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    /**
     * 根据用户名查询员工
     */
    Optional<Employee> findByUsername(String username);
    
    /**
     * 根据员工编号查询员工
     */
    Optional<Employee> findByEmployeeNumber(String employeeNumber);
    
    /**
     * 根据部门ID查询员工列表
     */
    List<Employee> findByDepartmentId(Long departmentId);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查员工编号是否存在
     */
    boolean existsByEmployeeNumber(String employeeNumber);
    
    /**
     * 根据员工姓名模糊查询
     */
    List<Employee> findByNameContaining(String name);
    
    /**
     * 根据职位模糊查询
     */
    List<Employee> findByPositionContaining(String position);
    
    /**
     * 根据员工姓名和职位模糊查询
     */
    List<Employee> findByNameContainingAndPositionContaining(String name, String position);
} 