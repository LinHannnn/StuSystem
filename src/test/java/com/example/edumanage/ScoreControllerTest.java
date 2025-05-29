package com.example.edumanage;

import com.example.edumanage.controller.ScoreController;
import com.example.edumanage.dto.ScoreDTO;
import com.example.edumanage.service.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScoreControllerTest {

    @Mock
    private ScoreService scoreService;

    @InjectMocks
    private ScoreController scoreController;

    private ScoreDTO scoreDTO;

    @BeforeEach
    void setUp() {
        scoreDTO = new ScoreDTO();
        scoreDTO.setId(1L);
        scoreDTO.setStudentId(1L);
        scoreDTO.setStudentName("张三");
        scoreDTO.setStudentCode("S001");
        scoreDTO.setCourseId(1L);
        scoreDTO.setCourseName("数学");
        scoreDTO.setExamType("FINAL");
        scoreDTO.setScore(new BigDecimal("85.5"));
        scoreDTO.setSemester("2025-SPRING");
        scoreDTO.setPassed(0); // 及格
        scoreDTO.setStatus(1);
    }

    @Test
    void testCreateScore() {
        // 模拟依赖方法
        when(scoreService.createScore(any(ScoreDTO.class))).thenReturn(scoreDTO);
        
        // 执行测试
        var result = scoreController.createScore(scoreDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getCode()); // 成功码
        assertEquals(scoreDTO, result.getData());
        
        // 验证交互
        verify(scoreService).createScore(any(ScoreDTO.class));
    }
    
    @Test
    void testCreateScoreWithException() {
        // 模拟依赖方法
        when(scoreService.createScore(any(ScoreDTO.class)))
                .thenThrow(new IllegalArgumentException("测试异常"));
        
        // 执行测试
        var result = scoreController.createScore(scoreDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getCode()); // 错误码
        assertEquals("测试异常", result.getMessage());
        
        // 验证交互
        verify(scoreService).createScore(any(ScoreDTO.class));
    }
    
    @Test
    void testGetScoreById() {
        // 模拟依赖方法
        when(scoreService.getScoreById(1L)).thenReturn(scoreDTO);
        
        // 执行测试
        var result = scoreController.getScoreById(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getCode()); // 成功码
        assertEquals(scoreDTO, result.getData());
        
        // 验证交互
        verify(scoreService).getScoreById(1L);
    }
    
    @Test
    void testGetAllScores() {
        // 模拟依赖方法
        ScoreDTO scoreDTO2 = new ScoreDTO();
        scoreDTO2.setId(2L);
        List<ScoreDTO> scoreDTOs = Arrays.asList(scoreDTO, scoreDTO2);
        when(scoreService.getAllScores()).thenReturn(scoreDTOs);
        
        // 执行测试
        var result = scoreController.getAllScores();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getCode()); // 成功码
        assertEquals(2, result.getData().size());
        
        // 验证交互
        verify(scoreService).getAllScores();
    }
    
    @Test
    void testGetAllScoresPaginated() {
        // 模拟依赖方法
        Page<ScoreDTO> scorePage = new PageImpl<>(Arrays.asList(scoreDTO));
        when(scoreService.getAllScores(any(Pageable.class))).thenReturn(scorePage);
        
        // 执行测试
        var result = scoreController.getAllScores(mock(Pageable.class));
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getCode()); // 成功码
        assertEquals(1, result.getData().getTotalElements());
        
        // 验证交互
        verify(scoreService).getAllScores(any(Pageable.class));
    }
    
    @Test
    void testDeleteScore() {
        // 模拟依赖方法
        doNothing().when(scoreService).deleteScore(anyLong());
        
        // 执行测试
        var result = scoreController.deleteScore(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getCode()); // 成功码
        
        // 验证交互
        verify(scoreService).deleteScore(1L);
    }
    
    @Test
    void testGetStudentAverageScoreBySemester() {
        // 模拟依赖方法
        when(scoreService.getStudentAverageScoreBySemester(1L, "2025-SPRING"))
                .thenReturn(85.5);
        
        // 执行测试
        var result = scoreController.getStudentAverageScoreBySemester(1L, "2025-SPRING");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getCode()); // 成功码
        Map<String, Double> data = result.getData();
        assertEquals(85.5, data.get("averageScore"));
        
        // 验证交互
        verify(scoreService).getStudentAverageScoreBySemester(1L, "2025-SPRING");
    }
} 