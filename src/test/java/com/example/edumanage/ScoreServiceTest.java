package com.example.edumanage;

import com.example.edumanage.dto.ScoreConverter;
import com.example.edumanage.dto.ScoreDTO;
import com.example.edumanage.exception.ResourceNotFoundException;
import com.example.edumanage.model.Course;
import com.example.edumanage.model.Score;
import com.example.edumanage.model.Student;
import com.example.edumanage.repository.CourseRepository;
import com.example.edumanage.repository.ScoreRepository;
import com.example.edumanage.repository.StudentRepository;
import com.example.edumanage.service.impl.ScoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScoreServiceTest {

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Spy
    private ScoreConverter scoreConverter = new ScoreConverter();

    @InjectMocks
    private ScoreServiceImpl scoreService;

    private Student student;
    private Course course;
    private Score score;
    private ScoreDTO scoreDTO;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        student = new Student();
        student.setId(1L);
        student.setStudentId("S001");
        student.setStudentName("张三");
        
        course = new Course();
        course.setId(1L);
        course.setCourseName("数学");
        
        score = new Score();
        score.setId(1L);
        score.setStudent(student);
        score.setCourse(course);
        score.setExamType("FINAL");
        score.setScore(new BigDecimal("85.5"));
        score.setSemester("2025-SPRING");
        score.setStatus(1);
        
        scoreDTO = new ScoreDTO();
        scoreDTO.setId(1L);
        scoreDTO.setStudentId(1L);
        scoreDTO.setCourseId(1L);
        scoreDTO.setExamType("FINAL");
        scoreDTO.setScore(new BigDecimal("85.5"));
        scoreDTO.setSemester("2025-SPRING");
    }

    @Test
    void testCreateScore() {
        // 模拟依赖方法
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(scoreRepository.findByStudentIdAndCourseIdAndExamTypeAndSemesterAndStatus(
                anyLong(), anyLong(), anyString(), anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(scoreRepository.save(any(Score.class))).thenReturn(score);
        
        // 执行测试
        ScoreDTO result = scoreService.createScore(scoreDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getStudentId());
        assertEquals("张三", result.getStudentName());
        assertEquals("数学", result.getCourseName());
        assertEquals(0, result.getPassed()); // 及格
        
        // 验证交互
        verify(scoreRepository).save(any(Score.class));
    }
    
    @Test
    void testCreateScoreWithExistingRecord() {
        // 模拟依赖方法
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(scoreRepository.findByStudentIdAndCourseIdAndExamTypeAndSemesterAndStatus(
                anyLong(), anyLong(), anyString(), anyString(), anyInt()))
                .thenReturn(Optional.of(score));
        
        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            scoreService.createScore(scoreDTO);
        });
        
        // 验证交互
        verify(scoreRepository, never()).save(any(Score.class));
    }
    
    @Test
    void testGetScoreById() {
        // 模拟依赖方法
        when(scoreRepository.findById(1L)).thenReturn(Optional.of(score));
        
        // 执行测试
        ScoreDTO result = scoreService.getScoreById(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        
        // 验证交互
        verify(scoreRepository).findById(1L);
    }
    
    @Test
    void testGetScoreByIdNotFound() {
        // 模拟依赖方法
        when(scoreRepository.findById(1L)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> {
            scoreService.getScoreById(1L);
        });
        
        // 验证交互
        verify(scoreRepository).findById(1L);
    }
    
    @Test
    void testGetAllScores() {
        // 模拟依赖方法
        Score score2 = new Score();
        score2.setId(2L);
        score2.setStudent(student);
        score2.setCourse(course);
        score2.setExamType("MID");
        score2.setScore(new BigDecimal("75.0"));
        
        List<Score> scoreList = Arrays.asList(score, score2);
        when(scoreRepository.findAll()).thenReturn(scoreList);
        
        // 执行测试
        List<ScoreDTO> results = scoreService.getAllScores();
        
        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 验证交互
        verify(scoreRepository).findAll();
    }
    
    @Test
    void testGetAllScoresPaginated() {
        // 模拟依赖方法
        Pageable pageable = PageRequest.of(0, 10);
        Page<Score> scorePage = new PageImpl<>(Arrays.asList(score));
        when(scoreRepository.findAll(pageable)).thenReturn(scorePage);
        
        // 执行测试
        Page<ScoreDTO> results = scoreService.getAllScores(pageable);
        
        // 验证结果
        assertNotNull(results);
        assertEquals(1, results.getTotalElements());
        
        // 验证交互
        verify(scoreRepository).findAll(pageable);
    }
    
    @Test
    void testDeleteScore() {
        // 模拟依赖方法
        when(scoreRepository.findById(1L)).thenReturn(Optional.of(score));
        
        // 执行测试
        scoreService.deleteScore(1L);
        
        // 验证交互
        verify(scoreRepository).findById(1L);
        verify(scoreRepository).save(any(Score.class));
        assertEquals(0, score.getStatus()); // 验证状态被设置为0
    }
    
    @Test
    void testGetStudentAverageScoreBySemester() {
        // 模拟依赖方法
        when(studentRepository.existsById(1L)).thenReturn(true);
        when(scoreRepository.getStudentAverageScoreBySemester(1L, "2025-SPRING"))
                .thenReturn(85.5);
        
        // 执行测试
        Double result = scoreService.getStudentAverageScoreBySemester(1L, "2025-SPRING");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(85.5, result);
        
        // 验证交互
        verify(scoreRepository).getStudentAverageScoreBySemester(1L, "2025-SPRING");
    }
} 