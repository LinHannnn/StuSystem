package com.example.edumanage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkExperienceDTO {
    private Long id;
    
    @NotBlank(message = "公司名称不能为空")
    private String companyName;
    
    private String position;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
} 