package com.example.edumanage.service;

import com.example.edumanage.dto.DepartmentRequest;
import com.example.edumanage.model.Department;
import com.example.edumanage.model.Employee;
import com.example.edumanage.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(DepartmentRequest request) {
        // 检查部门名称是否已存在
        if (departmentRepository.findByDepartmentName(request.getDepartmentName()).isPresent()) {
            throw new IllegalArgumentException("部门名称已存在");
        }

        Department department = new Department();
        department.setDepartmentName(request.getDepartmentName());
        
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("部门不存在"));
    }

    public Department getDepartmentByName(String departmentName) {
        return departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> new EntityNotFoundException("部门不存在：" + departmentName));
    }

    /**
     * 根据部门名称模糊查询部门
     */
    public List<Department> findDepartmentsByNameContaining(String departmentName) {
        return departmentRepository.findByDepartmentNameContaining(departmentName);
    }

    public Department updateDepartment(Long id, DepartmentRequest request) {
        Department department = getDepartmentById(id);

        // 检查更新的名称是否与其他部门冲突
        if (departmentRepository.existsByDepartmentNameAndIdNot(request.getDepartmentName(), id)) {
            throw new IllegalArgumentException("部门名称已存在");
        }

        department.setDepartmentName(request.getDepartmentName());

        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        if (!department.getEmployees().isEmpty()) {
            throw new IllegalStateException("部门下还有员工，无法删除");
        }
        departmentRepository.delete(department);
    }

    public List<Employee> getDepartmentEmployees(Long id) {
        Department department = getDepartmentById(id);
        return department.getEmployees();
    }

    public long getDepartmentEmployeeCount(Long id) {
        Department department = getDepartmentById(id);
        return department.getEmployees().size();
    }
} 