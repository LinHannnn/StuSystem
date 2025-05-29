package com.example.edumanage.service;

import com.example.edumanage.dto.ScoreDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ScoreService {
    
    // 创建成绩
    ScoreDTO createScore(ScoreDTO scoreDTO);
    
    // 更新成绩
    ScoreDTO updateScore(Long id, ScoreDTO scoreDTO);
    
    // 根据ID获取成绩
    ScoreDTO getScoreById(Long id);
    
    // 获取所有成绩（分页）
    Page<ScoreDTO> getAllScores(Pageable pageable);
    
    // 获取所有成绩（不分页）
    List<ScoreDTO> getAllScores();
    
    // 删除成绩
    void deleteScore(Long id);
    
    // 批量删除成绩
    void deleteScores(List<Long> ids);
    
    // 根据学生ID获取成绩
    List<ScoreDTO> getScoresByStudentId(Long studentId);
    
    // 根据学期获取所有成绩
    List<ScoreDTO> getScoresBySemester(String semester);
    
    // 根据学生ID和学期获取成绩
    List<ScoreDTO> getScoresByStudentIdAndSemester(Long studentId, String semester);
    
    // 获取学生在某学期的平均成绩
    Double getStudentAverageScoreBySemester(Long studentId, String semester);
    
    // 获取课程在某学期的平均成绩
    Double getCourseAverageScoreBySemester(Long courseId, String semester);
} 