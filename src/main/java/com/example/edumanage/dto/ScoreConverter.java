package com.example.edumanage.dto;

import com.example.edumanage.model.Course;
import com.example.edumanage.model.Score;
import com.example.edumanage.model.Student;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScoreConverter {

    public ScoreDTO toDTO(Score score) {
        if (score == null) {
            return null;
        }

        ScoreDTO dto = new ScoreDTO();
        dto.setId(score.getId());
        
        // 设置学生信息
        if (score.getStudent() != null) {
            dto.setStudentId(score.getStudent().getId());
            dto.setStudentName(score.getStudent().getStudentName());
            dto.setStudentCode(score.getStudent().getStudentId());
        }
        
        // 设置课程信息
        if (score.getCourse() != null) {
            dto.setCourseId(score.getCourse().getId());
            dto.setCourseName(score.getCourse().getCourseName());
        }
        
        dto.setExamType(score.getExamType());
        dto.setScore(score.getScore());
        dto.setSemester(score.getSemester());
        dto.setStatus(score.getStatus());
        dto.setCreateTime(score.getCreateTime());
        dto.setUpdateTime(score.getUpdateTime());
        
        // 计算是否及格（分数低于60分为不及格）
        // 0表示及格，1表示不及格
        if (score.getScore() != null) {
            dto.setPassed(score.getScore().compareTo(new BigDecimal("60")) >= 0 ? 0 : 1);
        }
        
        return dto;
    }

    public List<ScoreDTO> toDTOList(List<Score> scores) {
        return scores.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Score toEntity(ScoreDTO dto, Student student, Course course) {
        if (dto == null) {
            return null;
        }

        Score score = new Score();
        score.setId(dto.getId());
        score.setStudent(student);
        score.setCourse(course);
        score.setExamType(dto.getExamType());
        score.setScore(dto.getScore());
        score.setSemester(dto.getSemester());
        score.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        
        return score;
    }
} 