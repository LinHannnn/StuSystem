package com.example.edumanage.service.impl;

import com.example.edumanage.dto.EmployeeConverter;
import com.example.edumanage.dto.EmployeeDTO;
import com.example.edumanage.dto.EmployeeUpdateDTO;
import com.example.edumanage.exception.ResourceNotFoundException;
import com.example.edumanage.model.Department;
import com.example.edumanage.model.Employee;
import com.example.edumanage.repository.DepartmentRepository;
import com.example.edumanage.repository.EmployeeRepository;
import com.example.edumanage.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.dao.DataIntegrityViolationException;
import java.sql.SQLException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeConverter employeeConverter;
    private final Random random = new Random();

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, 
                              DepartmentRepository departmentRepository, 
                              EmployeeConverter employeeConverter) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeConverter = employeeConverter;
    }

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        // 检查用户名是否已存在
        if (isUsernameExists(employeeDTO.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 生成唯一的2位数员工编号
        String employeeNumber = generateUniqueEmployeeNumber();
        employeeDTO.setEmployeeNumber(employeeNumber);

        // 获取部门信息
        Department department = null;
        if (employeeDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
        }

        // 转换DTO到实体
        Employee employee = employeeConverter.toEntity(employeeDTO, department);
        
        // 处理工作经历
        employeeConverter.updateWorkExperiences(employee, employeeDTO.getWorkExperiences());
        
        // 保存实体
        employee = employeeRepository.save(employee);
        
        // 转换实体到DTO并返回
        return employeeConverter.toDTO(employee);
    }
    
    /**
     * 生成唯一的2位数员工编号
     * 使用年份+部门代码+随机数的方式
     */
    private String generateUniqueEmployeeNumber() {
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yy")); // 年份，如23
        
        // 尝试最多100次生成唯一编号
        for (int i = 0; i < 100; i++) {
            // 生成2位随机数
            int randomNum = 10 + random.nextInt(90); // 10-99之间的随机数
            String employeeNumber = prefix + String.format("%02d", randomNum); // 如2345
            
            // 检查是否已存在
            if (!isEmployeeNumberExists(employeeNumber)) {
                return employeeNumber;
            }
        }
        
        // 如果尝试多次仍无法生成唯一编号，则使用时间戳
        return prefix + System.currentTimeMillis() % 100;
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        // 检查员工是否存在
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("员工不存在"));

        // 如果用户名变更，检查新用户名是否已存在
        if (!existingEmployee.getUsername().equals(employeeDTO.getUsername()) 
                && isUsernameExists(employeeDTO.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 员工编号不允许修改，使用现有的员工编号
        employeeDTO.setEmployeeNumber(existingEmployee.getEmployeeNumber());

        // 获取部门信息
        Department department = null;
        if (employeeDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
        }

        // 更新员工信息
        existingEmployee.setUsername(employeeDTO.getUsername());
        // 员工编号保持不变
        existingEmployee.setName(employeeDTO.getName());
        existingEmployee.setGender(employeeDTO.getGender());
        existingEmployee.setAge(employeeDTO.getAge());
        existingEmployee.setPosition(employeeDTO.getPosition());
        existingEmployee.setSalary(employeeDTO.getSalary());
        existingEmployee.setHireDate(employeeDTO.getHireDate());
        existingEmployee.setDepartment(department);
        
        // 处理头像更新，如果提供了有效的base64头像
        if (employeeDTO.getAvatar() != null && !employeeDTO.getAvatar().isEmpty()) {
            try {
                validateBase64Image(employeeDTO.getAvatar());
                existingEmployee.setAvatar(employeeDTO.getAvatar());
                logger.info("已更新员工ID: {} 的头像", id);
            } catch (IllegalArgumentException e) {
                logger.error("头像无效: {}", e.getMessage());
                throw e;
            }
        }
        
        // 处理工作经历
        employeeConverter.updateWorkExperiences(existingEmployee, employeeDTO.getWorkExperiences());

        // 保存更新
        existingEmployee = employeeRepository.save(existingEmployee);
        
        // 转换并返回
        return employeeConverter.toDTO(existingEmployee);
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("员工不存在"));
        return employeeConverter.toDTO(employee);
    }

    @Override
    public EmployeeDTO getEmployeeByUsername(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("员工不存在"));
        return employeeConverter.toDTO(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("员工不存在");
        }
        
        try {
        employeeRepository.deleteById(id);
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
                    throw new RuntimeException("教师正在任课，不可删除");
                }
            }
            
            // 如果不是特定的外键错误，则重新抛出原异常
            throw e;
        }
    }

    @Override
    public Page<EmployeeDTO> getEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return employees.map(employeeConverter::toDTO);
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
        
        List<Employee> employees = department.getEmployees();
        return employeeConverter.toDTOList(employees);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return employeeRepository.existsByUsername(username);
    }
    
    @Override
    public boolean isEmployeeNumberExists(String employeeNumber) {
        return employeeRepository.existsByEmployeeNumber(employeeNumber);
    }

    @Override
    public List<EmployeeDTO> searchEmployeesByName(String name) {
        List<Employee> employees = employeeRepository.findByNameContaining(name);
        return employeeConverter.toDTOList(employees);
    }
    
    @Override
    public List<EmployeeDTO> searchEmployeesByPosition(String position) {
        List<Employee> employees = employeeRepository.findByPositionContaining(position);
        return employeeConverter.toDTOList(employees);
    }
    
    @Override
    public List<EmployeeDTO> searchEmployeesByNameAndPosition(String name, String position) {
        List<Employee> employees = employeeRepository.findByNameContainingAndPositionContaining(name, position);
        return employeeConverter.toDTOList(employees);
    }
    
    @Override
    public List<EmployeeDTO> advancedSearch(String name, String position, Long departmentId) {
        // 使用JPA规范构建动态查询
        org.springframework.data.jpa.domain.Specification<Employee> spec = 
            (root, query, criteriaBuilder) -> {
                java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
                
                // 添加姓名模糊查询条件
                if (name != null && !name.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
                }
                
                // 添加职位模糊查询条件
                if (position != null && !position.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("position"), "%" + position + "%"));
                }
                
                // 添加部门精确查询条件
                if (departmentId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("department").get("id"), departmentId));
                }
                
                // 组合所有条件
                return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            };
        
        List<Employee> employees = employeeRepository.findAll(spec);
        return employeeConverter.toDTOList(employees);
    }

    @Override
    @Transactional
    public String uploadAvatar(Long employeeId, MultipartFile file) {
        // 检查员工是否存在
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("员工不存在"));
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的文件");
        }
        
        logger.info("正在为员工ID: {} 上传头像", employeeId);
        
        try {
            // 读取文件字节
            byte[] fileBytes = file.getBytes();
            
            // 检查文件大小
            if (fileBytes.length > 2 * 1024 * 1024) { // 2MB
                throw new IllegalArgumentException("头像图片不能超过2MB");
            }
            
            // 获取文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("只允许上传图片文件");
            }
            
            // 转换为base64
            String base64Image = Base64.getEncoder().encodeToString(fileBytes);
            
            // 构建完整的base64字符串（包含数据类型前缀）
            String avatarBase64 = "data:" + contentType + ";base64," + base64Image;
            
            // 验证base64
            validateBase64Image(avatarBase64);
            
            // 保存头像到员工实体
            employee.setAvatar(avatarBase64);
            employeeRepository.save(employee);
            
            logger.info("员工ID: {} 的头像上传成功", employeeId);
            
            return avatarBase64;
        } catch (IOException e) {
            logger.error("读取上传文件时出错: {}", e.getMessage());
            throw new RuntimeException("无法读取上传文件: " + e.getMessage());
        }
    }
    
    /**
     * 验证base64格式的图片数据
     */
    private void validateBase64Image(String base64Content) {
        if (base64Content == null || !base64Content.startsWith("data:image/")) {
            throw new IllegalArgumentException("头像格式不正确，必须是base64编码的图片");
        }
        
        // 获取图片类型
        String[] parts = base64Content.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("头像格式不正确，base64编码格式有误");
        }
        
        // 解码并检查大小
        String base64Image = parts[1];
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(base64Image);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("头像base64编码无效: " + e.getMessage());
        }
        
        // 限制头像大小为2MB
        if (imageBytes.length > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("头像图片不能超过2MB");
        }
    }

    @Override
    @Transactional
    public EmployeeDTO partialUpdateEmployee(Long id, EmployeeUpdateDTO updateDTO) {
        // 检查员工是否存在
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("员工不存在"));
        
        logger.info("开始部分更新员工信息，ID: {}", id);
        
        // 只更新提供的字段
        boolean hasUpdates = false;
        
        // 用户名更新（如果提供且不同）
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(existingEmployee.getUsername())) {
            // 检查新用户名是否已存在
            if (isUsernameExists(updateDTO.getUsername())) {
                throw new IllegalArgumentException("用户名已存在");
            }
            existingEmployee.setUsername(updateDTO.getUsername());
            hasUpdates = true;
            logger.info("更新员工用户名: {}", updateDTO.getUsername());
        }
        
        // 姓名更新
        if (updateDTO.getName() != null) {
            existingEmployee.setName(updateDTO.getName());
            hasUpdates = true;
            logger.info("更新员工姓名: {}", updateDTO.getName());
        }
        
        // 性别更新
        if (updateDTO.getGender() != null) {
            existingEmployee.setGender(updateDTO.getGender());
            hasUpdates = true;
            logger.info("更新员工性别: {}", updateDTO.getGender());
        }
        
        // 年龄更新
        if (updateDTO.getAge() != null) {
            existingEmployee.setAge(updateDTO.getAge());
            hasUpdates = true;
            logger.info("更新员工年龄: {}", updateDTO.getAge());
        }
        
        // 部门更新
        if (updateDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(updateDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
            existingEmployee.setDepartment(department);
            hasUpdates = true;
            logger.info("更新员工部门: {}", department.getDepartmentName());
        }
        
        // 职位更新
        if (updateDTO.getPosition() != null) {
            existingEmployee.setPosition(updateDTO.getPosition());
            hasUpdates = true;
            logger.info("更新员工职位: {}", updateDTO.getPosition());
        }
        
        // 薪资更新
        if (updateDTO.getSalary() != null) {
            existingEmployee.setSalary(updateDTO.getSalary());
            hasUpdates = true;
            logger.info("更新员工薪资");
        }
        
        // 入职日期更新
        if (updateDTO.getHireDate() != null) {
            existingEmployee.setHireDate(updateDTO.getHireDate());
            hasUpdates = true;
            logger.info("更新员工入职日期: {}", updateDTO.getHireDate());
        }
        
        // 头像更新
        if (updateDTO.getAvatar() != null) {
            try {
                validateBase64Image(updateDTO.getAvatar());
                existingEmployee.setAvatar(updateDTO.getAvatar());
                hasUpdates = true;
                logger.info("更新员工头像");
            } catch (IllegalArgumentException e) {
                logger.error("头像无效: {}", e.getMessage());
                throw e;
            }
        }
        
        // 工作经历更新
        if (updateDTO.getWorkExperiences() != null) {
            employeeConverter.updateWorkExperiences(existingEmployee, updateDTO.getWorkExperiences());
            hasUpdates = true;
            logger.info("更新员工工作经历");
        }
        
        if (!hasUpdates) {
            logger.info("没有提供任何需要更新的字段");
        }
        
        // 保存更新
        existingEmployee = employeeRepository.save(existingEmployee);
        logger.info("员工信息更新成功");
        
        // 转换并返回
        return employeeConverter.toDTO(existingEmployee);
    }
} 