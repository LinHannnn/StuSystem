package com.example.edumanage.dto;

import com.example.edumanage.model.WorkExperience;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkExperienceConverter {

    public WorkExperienceDTO toDTO(WorkExperience workExperience) {
        if (workExperience == null) {
            return null;
        }

        WorkExperienceDTO dto = new WorkExperienceDTO();
        dto.setId(workExperience.getId());
        dto.setCompanyName(workExperience.getCompanyName());
        dto.setPosition(workExperience.getPosition());
        dto.setStartDate(workExperience.getStartDate());
        dto.setEndDate(workExperience.getEndDate());

        return dto;
    }

    public List<WorkExperienceDTO> toDTOList(List<WorkExperience> workExperiences) {
        return workExperiences.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WorkExperience toEntity(WorkExperienceDTO dto) {
        if (dto == null) {
            return null;
        }

        WorkExperience workExperience = new WorkExperience();
        
        // 如果是更新操作，需要设置ID
        if (dto.getId() != null) {
            workExperience.setId(dto.getId());
        }
        
        workExperience.setCompanyName(dto.getCompanyName());
        workExperience.setPosition(dto.getPosition());
        workExperience.setStartDate(dto.getStartDate());
        workExperience.setEndDate(dto.getEndDate());

        return workExperience;
    }

    public List<WorkExperience> toEntityList(List<WorkExperienceDTO> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 