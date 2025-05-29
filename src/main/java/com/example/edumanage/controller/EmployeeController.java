package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.dto.EmployeeDTO;
import com.example.edumanage.dto.EmployeeUpdateDTO;
import com.example.edumanage.model.Department;
import com.example.edumanage.model.Employee;
import com.example.edumanage.model.Employee.Gender;
import com.example.edumanage.repository.DepartmentRepository;
import com.example.edumanage.repository.EmployeeRepository;
import com.example.edumanage.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('ADMIN')") // 类级别权限控制，要求ADMIN角色
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 创建员工
     */
    @PostMapping
    public ResponseEntity<Result<EmployeeDTO>> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(createdEmployee));
    }
    
    /**
     * 简化版创建员工接口（用于调试）
     */
    @PostMapping("/simple")
    public ResponseEntity<Result<Map<String, Object>>> createSimpleEmployee(@RequestBody Map<String, Object> request) {
        try {
            // 提取基本信息
            String username = (String) request.get("username");
            String name = (String) request.get("name");
            String genderStr = (String) request.get("gender");
            Gender gender = Gender.valueOf(genderStr);
            String position = (String) request.get("position");
            
            // 检查用户名是否存在
            if (employeeRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(Result.fail("用户名已存在"));
            }
            
            // 创建DTO对象，通过服务来自动生成员工编号
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setUsername(username);
            employeeDTO.setName(name);
            employeeDTO.setGender(gender);
            employeeDTO.setPosition(position);
            
            // 处理部门
            if (request.containsKey("departmentId")) {
                Long departmentId = Long.valueOf(request.get("departmentId").toString());
                employeeDTO.setDepartmentId(departmentId);
            }
            
            // 使用服务创建员工
            EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
            
            // 返回创建的员工信息
            Map<String, Object> response = new HashMap<>();
            response.put("id", createdEmployee.getId());
            response.put("username", createdEmployee.getUsername());
            response.put("employeeNumber", createdEmployee.getEmployeeNumber());
            response.put("name", createdEmployee.getName());
            response.put("gender", createdEmployee.getGender().name());
            response.put("position", createdEmployee.getPosition());
            response.put("departmentId", createdEmployee.getDepartmentId());
            response.put("departmentName", createdEmployee.getDepartmentName());
            response.put("createTime", createdEmployee.getCreateTime());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("创建员工失败: " + e.getMessage() + " | " + e.getClass().getName()));
        }
    }

    /**
     * 简化版创建员工接口，支持base64头像和工作经历（用于调试）
     */
    @PostMapping("/simple/avatar")
    public ResponseEntity<Result<Map<String, Object>>> createSimpleEmployeeWithAvatar(@RequestBody Map<String, Object> request) {
        try {
            // 提取基本信息
            String username = (String) request.get("username");
            String name = (String) request.get("name");
            String genderStr = (String) request.get("gender");
            Gender gender = Gender.valueOf(genderStr);
            String position = (String) request.get("position");
            String avatar = (String) request.get("avatar"); // 添加头像字段
            
            // 检查用户名是否存在
            if (employeeRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(Result.fail("用户名已存在"));
            }
            
            // 创建DTO对象，通过服务来自动生成员工编号
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setUsername(username);
            employeeDTO.setName(name);
            employeeDTO.setGender(gender);
            employeeDTO.setPosition(position);
            
            // 处理头像
            if (avatar != null && !avatar.isEmpty()) {
                if (!avatar.startsWith("data:image/")) {
                    return ResponseEntity.badRequest().body(Result.fail("头像必须是base64格式的图片(以data:image/开头)"));
                }
                employeeDTO.setAvatar(avatar);
            }
            
            // 处理工作经历
            if (request.containsKey("workExperiences") && request.get("workExperiences") instanceof List) {
                List<Map<String, Object>> workExperiencesList = (List<Map<String, Object>>) request.get("workExperiences");
                if (workExperiencesList != null && !workExperiencesList.isEmpty()) {
                    List<com.example.edumanage.dto.WorkExperienceDTO> workExperiences = new java.util.ArrayList<>();
                    
                    for (Map<String, Object> expMap : workExperiencesList) {
                        com.example.edumanage.dto.WorkExperienceDTO exp = new com.example.edumanage.dto.WorkExperienceDTO();
                        
                        if (expMap.containsKey("companyName")) {
                            exp.setCompanyName((String) expMap.get("companyName"));
                        }
                        
                        if (expMap.containsKey("position")) {
                            exp.setPosition((String) expMap.get("position"));
                        }
                        
                        if (expMap.containsKey("startDate")) {
                            String startDateStr = (String) expMap.get("startDate");
                            if (startDateStr != null && !startDateStr.isEmpty()) {
                                exp.setStartDate(java.time.LocalDate.parse(startDateStr));
                            }
                        }
                        
                        if (expMap.containsKey("endDate")) {
                            String endDateStr = (String) expMap.get("endDate");
                            if (endDateStr != null && !endDateStr.isEmpty()) {
                                exp.setEndDate(java.time.LocalDate.parse(endDateStr));
                            }
                        }
                        
                        workExperiences.add(exp);
                    }
                    
                    employeeDTO.setWorkExperiences(workExperiences);
                }
            }
            
            // 处理部门
            if (request.containsKey("departmentId")) {
                Long departmentId = Long.valueOf(request.get("departmentId").toString());
                employeeDTO.setDepartmentId(departmentId);
            }
            
            // 处理薪资
            if (request.containsKey("salary")) {
                Object salaryObj = request.get("salary");
                if (salaryObj != null) {
                    if (salaryObj instanceof Number) {
                        employeeDTO.setSalary(new java.math.BigDecimal(salaryObj.toString()));
                    } else if (salaryObj instanceof String && !((String) salaryObj).isEmpty()) {
                        employeeDTO.setSalary(new java.math.BigDecimal((String) salaryObj));
                    }
                }
            }
            
            // 处理入职日期
            if (request.containsKey("hireDate")) {
                String hireDateStr = (String) request.get("hireDate");
                if (hireDateStr != null && !hireDateStr.isEmpty()) {
                    employeeDTO.setHireDate(java.time.LocalDate.parse(hireDateStr));
                }
            }
            
            // 使用服务创建员工
            EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
            
            // 返回创建的员工信息
            Map<String, Object> response = new HashMap<>();
            response.put("id", createdEmployee.getId());
            response.put("username", createdEmployee.getUsername());
            response.put("employeeNumber", createdEmployee.getEmployeeNumber());
            response.put("name", createdEmployee.getName());
            response.put("gender", createdEmployee.getGender().name());
            response.put("position", createdEmployee.getPosition());
            response.put("departmentId", createdEmployee.getDepartmentId());
            response.put("departmentName", createdEmployee.getDepartmentName());
            response.put("salary", createdEmployee.getSalary());
            response.put("hireDate", createdEmployee.getHireDate());
            response.put("createTime", createdEmployee.getCreateTime());
            
            if (createdEmployee.getAvatar() != null) {
                response.put("hasAvatar", true);
            }
            
            if (createdEmployee.getWorkExperiences() != null && !createdEmployee.getWorkExperiences().isEmpty()) {
                response.put("workExperiences", createdEmployee.getWorkExperiences());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Result.success(response));
        } catch (Exception e) {
            logger.error("创建员工失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("创建员工失败: " + e.getMessage() + " | " + e.getClass().getName()));
        }
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<EmployeeDTO>> updateEmployee(@PathVariable Long id, 
                                                  @Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(Result.success(updatedEmployee));
    }

    /**
     * 部分更新员工信息（只更新提供的字段）
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Result<EmployeeDTO>> partialUpdateEmployee(@PathVariable Long id, 
                                                  @Valid @RequestBody EmployeeUpdateDTO updateDTO) {
        logger.info("接收到部分更新员工请求，ID: {}", id);
        EmployeeDTO updatedEmployee = employeeService.partialUpdateEmployee(id, updateDTO);
        return ResponseEntity.ok(Result.success(updatedEmployee));
    }

    /**
     * 获取员工详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<EmployeeDTO>> getEmployee(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(Result.success(employee));
    }

    /**
     * 根据用户名获取员工，自动支持精确查询和模糊查询
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Result<Object>> getEmployeeByUsername(@PathVariable String username) {
        // 先尝试精确查询
        try {
            EmployeeDTO employee = employeeService.getEmployeeByUsername(username);
            return ResponseEntity.ok(Result.success(employee));
        } catch (RuntimeException e) {
            // 精确查询失败，尝试模糊查询
            List<EmployeeDTO> employees = employeeService.searchEmployeesByName(username);
            if (!employees.isEmpty()) {
                return ResponseEntity.ok(Result.success(employees));
            } else {
                // 如果模糊查询也没有结果，返回原始错误
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.fail(e.getMessage()));
            }
        }
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteEmployee(@PathVariable Long id) {
        try {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(Result.success());
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            // 检查是否是我们期望的错误消息
            if (errorMessage.contains("foreign key constraint fails")) {
                // 替换为更友好的错误消息
                errorMessage = "教师正在任课，不可删除";
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Result.fail(errorMessage));
        }
    }

    /**
     * 分页获取员工列表
     */
    @GetMapping
    public ResponseEntity<Result<Page<EmployeeDTO>>> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                                   Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<EmployeeDTO> employees = employeeService.getEmployees(pageable);
        return ResponseEntity.ok(Result.success(employees));
    }

    /**
     * 获取指定部门下的所有员工
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<Result<List<EmployeeDTO>>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(Result.success(employees));
    }

    /**
     * 检查用户名是否已存在
     */
    @GetMapping("/check-username/{username}")
    @PreAuthorize("permitAll()") // 这个接口允许所有人访问，方便前端在注册时验证
    public ResponseEntity<Result<Boolean>> checkUsername(@PathVariable String username) {
        boolean exists = employeeService.isUsernameExists(username);
        return ResponseEntity.ok(Result.success(exists));
    }
    
    /**
     * 搜索员工接口
     */
    @GetMapping("/search")
    public ResponseEntity<Result<List<EmployeeDTO>>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Long departmentId) {
        
        List<EmployeeDTO> employees;
        
        if (name != null && position != null) {
            // 同时按姓名和职位模糊查询
            employees = employeeService.searchEmployeesByNameAndPosition(name, position);
        } else if (name != null) {
            // 按姓名模糊查询
            employees = employeeService.searchEmployeesByName(name);
        } else if (position != null) {
            // 按职位模糊查询
            employees = employeeService.searchEmployeesByPosition(position);
        } else if (departmentId != null) {
            // 按部门精确查询
            employees = employeeService.getEmployeesByDepartment(departmentId);
        } else {
            // 无搜索条件，返回前10个员工
            employees = employeeService.getEmployees(PageRequest.of(0, 10, Sort.by("id"))).getContent();
        }
        
        return ResponseEntity.ok(Result.success(employees));
    }
    
    /**
     * 上传员工头像（文件上传方式）
     */
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Result<String>> uploadAvatar(@PathVariable Long id, 
                                                 @RequestParam("file") MultipartFile file) {
        try {
            logger.info("接收到文件上传请求，员工ID: {}，文件名: {}, 大小: {} bytes", 
                      id, file.getOriginalFilename(), file.getSize());

            if (file.isEmpty()) {
                logger.warn("上传的文件为空");
                return ResponseEntity.badRequest().body(Result.fail("上传的文件为空"));
            }

            // 检查文件类型
            String contentType = file.getContentType();
            logger.info("文件类型: {}", contentType);
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.warn("不支持的文件类型: {}", contentType);
                return ResponseEntity.badRequest().body(Result.fail("只支持图片文件上传"));
            }

            String avatarUrl = employeeService.uploadAvatar(id, file);
            logger.info("头像上传成功");
            return ResponseEntity.ok(Result.success(avatarUrl));
        } catch (Exception e) {
            logger.error("上传头像失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("上传头像失败: " + e.getMessage() + " [" + e.getClass().getName() + "]"));
        }
    }
    
    /**
     * 上传员工头像（Base64方式）
     */
    @PutMapping("/{id}/avatar/base64")
    public ResponseEntity<Result<String>> uploadAvatarBase64(@PathVariable Long id, 
                                                      @RequestBody Map<String, String> request) {
        try {
            logger.info("接收到Base64头像上传请求，员工ID: {}", id);
            
            String base64Avatar = request.get("avatar");
            if (base64Avatar == null || base64Avatar.isEmpty()) {
                logger.warn("头像数据为空");
                return ResponseEntity.badRequest().body(Result.fail("头像数据不能为空"));
            }
            
            logger.info("验证base64头像数据格式");
            // 验证base64格式
            if (!base64Avatar.startsWith("data:image/")) {
                logger.warn("头像格式不正确: {}", base64Avatar.substring(0, Math.min(20, base64Avatar.length())) + "...");
                return ResponseEntity.badRequest().body(Result.fail("头像必须是base64格式的图片"));
            }
            
            // 获取员工
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("员工不存在"));
            logger.info("找到员工: {}", employee.getName());
            
            // 验证并保存头像
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setAvatar(base64Avatar);
            
            // 更新用户，这会触发头像验证
            employeeDTO.setId(employee.getId());
            employeeDTO.setUsername(employee.getUsername());
            employeeDTO.setName(employee.getName());
            employeeDTO.setGender(employee.getGender());
            
            if (employee.getDepartment() != null) {
                employeeDTO.setDepartmentId(employee.getDepartment().getId());
            }
            
            logger.info("开始更新员工头像");
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
            logger.info("头像更新成功");
            
            return ResponseEntity.ok(Result.success("头像上传成功"));
        } catch (Exception e) {
            logger.error("上传头像失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("上传头像失败: " + e.getMessage() + " [" + e.getClass().getName() + "]"));
        }
    }
    
    /**
     * 获取员工头像
     */
    @GetMapping("/{id}/avatar")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Result<String>> getAvatar(@PathVariable Long id) {
        try {
            logger.info("接收到获取头像请求，员工ID: {}", id);
            
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("员工不存在"));
            logger.info("找到员工: {}", employee.getName());
            
            if (employee.getAvatar() == null || employee.getAvatar().isEmpty()) {
                logger.warn("员工没有头像");
                return ResponseEntity.ok(Result.fail("员工没有上传头像"));
            }
            
            logger.info("返回头像数据，长度: {} 字符", employee.getAvatar().length());
            return ResponseEntity.ok(Result.success(employee.getAvatar()));
        } catch (Exception e) {
            logger.error("获取头像失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.fail("获取头像失败: " + e.getMessage() + " [" + e.getClass().getName() + "]"));
        }
    }
    
    /**
     * 检查头像配置
     */
    @GetMapping("/avatar/check-config")
    public ResponseEntity<Result<Map<String, Object>>> checkAvatarConfig() {
        logger.info("接收到检查头像配置请求");
        
        Map<String, Object> config = new HashMap<>();
        config.put("status", "ok");
        config.put("storageType", "base64");
        config.put("maxSize", "2MB");
        config.put("supportedFormats", 
                   new String[]{"jpeg", "jpg", "png", "gif", "webp", "svg"});
        
        // 添加更多配置信息
        config.put("base64Api", "/api/employees/{id}/avatar/base64");
        config.put("uploadApi", "/api/employees/{id}/avatar");
        config.put("getAvatarApi", "/api/employees/{id}/avatar");
        config.put("apiVersion", "1.0");
        config.put("timestamp", System.currentTimeMillis());
        
        logger.info("返回头像配置信息");
        return ResponseEntity.ok(Result.success(config));
    }

    /**
     * 根据员工姓名获取员工，自动支持精确查询和模糊查询
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Result<Object>> getEmployeeByName(@PathVariable String name) {
        // 先尝试模糊查询
        List<EmployeeDTO> employees = employeeService.searchEmployeesByName(name);
        
        if (employees.isEmpty()) {
            // 如果模糊查询没有结果，返回错误
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.fail("未找到姓名包含 '" + name + "' 的员工"));
        } else if (employees.size() == 1 && employees.get(0).getName().equals(name)) {
            // 如果只有一个结果且完全匹配，返回单个对象
            return ResponseEntity.ok(Result.success(employees.get(0)));
        } else {
            // 否则返回列表
            return ResponseEntity.ok(Result.success(employees));
        }
    }
} 