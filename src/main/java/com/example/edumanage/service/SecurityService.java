package com.example.edumanage.service;

import com.example.edumanage.model.Employee;
import com.example.edumanage.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public boolean isEmployeeInDepartment(Long departmentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return employeeRepository.findByUsername(username)
                .map(employee -> employee.getDepartment() != null && 
                                employee.getDepartment().getId().equals(departmentId))
                .orElse(false);
    }
} 