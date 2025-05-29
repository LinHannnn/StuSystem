package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.dto.DepartmentRequest;
import com.example.edumanage.dto.DepartmentResponse;
import com.example.edumanage.model.Department;
import com.example.edumanage.model.Employee;
import com.example.edumanage.repository.DepartmentRepository;
import com.example.edumanage.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dpt/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        Department department = departmentService.createDepartment(request);
        return Result.success(convertToResponse(department));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Result<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> responses = departmentService.getAllDepartments()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return Result.success(responses);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public Result<?> searchDepartment(
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false, defaultValue = "false") Boolean exact) {
        
        if (departmentName != null && !departmentName.isEmpty()) {
            if (Boolean.TRUE.equals(exact)) {
                // 精确查询
                try {
                    Department department = departmentService.getDepartmentByName(departmentName);
                    return Result.success(convertToResponse(department));
                } catch (EntityNotFoundException e) {
                    return Result.fail(e.getMessage());
                }
            } else {
                // 模糊查询
                List<Department> departments = departmentService.findDepartmentsByNameContaining(departmentName);
                List<DepartmentResponse> responses = departments.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
                return Result.success(responses);
            }
        } else {
            // 如果没有提供部门名称，返回所有部门
            return getAllDepartments();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(convertToResponse(department));
    }

    @GetMapping("/name/{departmentName}")
    @PreAuthorize("isAuthenticated()")
    public Result<Object> getDepartmentByName(@PathVariable String departmentName) {
        // 先尝试精确查询
        try {
            Department department = departmentService.getDepartmentByName(departmentName);
            return Result.success(convertToResponse(department));
        } catch (EntityNotFoundException e) {
            // 精确查询失败，尝试模糊查询
            List<Department> departments = departmentService.findDepartmentsByNameContaining(departmentName);
            
            if (!departments.isEmpty()) {
                // 模糊查询有结果
                List<DepartmentResponse> responses = departments.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
                return Result.success(responses);
            } else {
                // 如果模糊查询也没有结果，返回原始错误
                return Result.fail(e.getMessage());
            }
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public Result<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        Department department = departmentService.updateDepartment(id, request);
        return Result.success(convertToResponse(department));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("删除部门失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR') or @securityService.isEmployeeInDepartment(#id)")
    public ResponseEntity<List<Employee>> getDepartmentEmployees(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentEmployees(id));
    }

    @GetMapping("/{id}/employees/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getDepartmentEmployeeCount(@PathVariable Long id) {
        long count = departmentService.getDepartmentEmployeeCount(id);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Result<Boolean>> checkDepartmentExists(@PathVariable Long id) {
        boolean exists = departmentRepository.existsById(id);
        return ResponseEntity.ok(Result.success(exists));
    }

    private DepartmentResponse convertToResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setDepartmentName(department.getDepartmentName());
        response.setCreateTime(department.getCreateTime());
        response.setUpdateTime(department.getUpdateTime());
        return response;
    }
} 