package com.example.edumanage.dto;

import com.example.edumanage.model.Department;
import com.example.edumanage.model.Employee;
import com.example.edumanage.model.WorkExperience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeConverter {

    private final WorkExperienceConverter workExperienceConverter;

    @Autowired
    public EmployeeConverter(WorkExperienceConverter workExperienceConverter) {
        this.workExperienceConverter = workExperienceConverter;
    }

    public EmployeeDTO toDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setUsername(employee.getUsername());
        dto.setEmployeeNumber(employee.getEmployeeNumber());
        dto.setName(employee.getName());
        dto.setGender(employee.getGender());
        dto.setAge(employee.getAge());
        dto.setPosition(employee.getPosition());
        dto.setSalary(employee.getSalary());
        dto.setHireDate(employee.getHireDate());
        dto.setAvatar(employee.getAvatar());
        dto.setCreateTime(employee.getCreateTime());
        dto.setUpdateTime(employee.getUpdateTime());

        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
            dto.setDepartmentName(employee.getDepartment().getDepartmentName());
        }

        // 转换工作经历
        if (employee.getWorkExperiences() != null && !employee.getWorkExperiences().isEmpty()) {
            dto.setWorkExperiences(
                employee.getWorkExperiences().stream()
                    .map(workExperienceConverter::toDTO)
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public List<EmployeeDTO> toDTOList(List<Employee> employees) {
        return employees.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Employee toEntity(EmployeeDTO dto, Department department) {
        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();
        
        // 如果是更新操作，需要设置ID
        if (dto.getId() != null) {
            employee.setId(dto.getId());
        }
        
        employee.setUsername(dto.getUsername());
        employee.setEmployeeNumber(dto.getEmployeeNumber());
        employee.setName(dto.getName());
        employee.setGender(dto.getGender());
        employee.setAge(dto.getAge());
        employee.setPosition(dto.getPosition());
        employee.setSalary(dto.getSalary());
        employee.setHireDate(dto.getHireDate());
        employee.setAvatar(dto.getAvatar());
        employee.setDepartment(department);

        return employee;
    }
    
    public void updateWorkExperiences(Employee employee, List<WorkExperienceDTO> workExperienceDTOs) {
        if (workExperienceDTOs == null || workExperienceDTOs.isEmpty()) {
            return;
        }
        
        // 清空现有的工作经历并添加新的
        employee.getWorkExperiences().clear();
        
        for (WorkExperienceDTO workExperienceDTO : workExperienceDTOs) {
            WorkExperience workExperience = workExperienceConverter.toEntity(workExperienceDTO);
            employee.addWorkExperience(workExperience);
        }
    }
} 