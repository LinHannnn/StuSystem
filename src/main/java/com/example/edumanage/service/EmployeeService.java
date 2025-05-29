package com.example.edumanage.service;

import com.example.edumanage.dto.EmployeeDTO;
import com.example.edumanage.dto.EmployeeUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    /**
     * 创建员工
     */
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    
    /**
     * 更新员工信息
     */
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    
    /**
     * 部分更新员工信息，只更新提供的字段
     * @param id 员工ID
     * @param updateDTO 包含需要更新字段的DTO
     * @return 更新后的员工DTO
     */
    EmployeeDTO partialUpdateEmployee(Long id, EmployeeUpdateDTO updateDTO);
    
    /**
     * 获取员工详情
     */
    EmployeeDTO getEmployeeById(Long id);
    
    /**
     * 根据用户名获取员工
     */
    EmployeeDTO getEmployeeByUsername(String username);
    
    /**
     * 删除员工
     */
    void deleteEmployee(Long id);
    
    /**
     * 分页获取员工列表
     */
    Page<EmployeeDTO> getEmployees(Pageable pageable);
    
    /**
     * 获取指定部门下的所有员工
     */
    List<EmployeeDTO> getEmployeesByDepartment(Long departmentId);
    
    /**
     * 检查用户名是否已存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查员工编号是否已存在
     */
    boolean isEmployeeNumberExists(String employeeNumber);
    
    /**
     * 上传员工头像
     */
    String uploadAvatar(Long employeeId, MultipartFile file);
    
    /**
     * 根据员工姓名模糊查询
     */
    List<EmployeeDTO> searchEmployeesByName(String name);
    
    /**
     * 根据职位模糊查询
     */
    List<EmployeeDTO> searchEmployeesByPosition(String position);
    
    /**
     * 根据员工姓名和职位模糊查询
     */
    List<EmployeeDTO> searchEmployeesByNameAndPosition(String name, String position);
    
    /**
     * 高级查询 - 支持多字段组合查询
     */
    List<EmployeeDTO> advancedSearch(String name, String position, Long departmentId);
} 