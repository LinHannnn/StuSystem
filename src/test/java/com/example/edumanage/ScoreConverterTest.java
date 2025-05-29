package com.example.edumanage;

import com.example.edumanage.dto.ScoreConverter;
import com.example.edumanage.dto.ScoreDTO;
import com.example.edumanage.model.Course;
import com.example.edumanage.model.Score;
import com.example.edumanage.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreConverterTest {

    private ScoreConverter scoreConverter;
    private Student student;
    private Course course;
    private Score score;

    @BeforeEach
    void setUp() {
        scoreConverter = new ScoreConverter();
        
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
    }

    @Test
    void testToDTOWithNullScore() {
        ScoreDTO dto = scoreConverter.toDTO(null);
        assertNull(dto);
    }

    @Test
    void testToDTO() {
        ScoreDTO dto = scoreConverter.toDTO(score);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getStudentId());
        assertEquals("张三", dto.getStudentName());
        assertEquals("S001", dto.getStudentCode());
        assertEquals(1L, dto.getCourseId());
        assertEquals("数学", dto.getCourseName());
        assertEquals("FINAL", dto.getExamType());
        assertEquals(0, new BigDecimal("85.5").compareTo(dto.getScore()));
        assertEquals("2025-SPRING", dto.getSemester());
        assertEquals(1, dto.getStatus());
        assertEquals(0, dto.getPassed()); // 及格
    }

    @ParameterizedTest
    @CsvSource({
        "59.99, 1", // 不及格
        "60, 0",    // 及格
        "60.01, 0", // 及格
        "100, 0"    // 及格
    })
    void testPassedLogic(BigDecimal scoreValue, int expectedPassed) {
        score.setScore(scoreValue);
        ScoreDTO dto = scoreConverter.toDTO(score);
        assertEquals(expectedPassed, dto.getPassed());
    }

    @Test
    void testToDTOList() {
        Score score2 = new Score();
        score2.setId(2L);
        score2.setStudent(student);
        score2.setCourse(course);
        score2.setExamType("MID");
        score2.setScore(new BigDecimal("75.0"));
        score2.setSemester("2025-SPRING");
        score2.setStatus(1);
        
        List<Score> scores = Arrays.asList(score, score2);
        List<ScoreDTO> dtos = scoreConverter.toDTOList(scores);
        
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(2L, dtos.get(1).getId());
    }

    @Test
    void testToEntity() {
        ScoreDTO dto = new ScoreDTO();
        dto.setId(1L);
        dto.setStudentId(1L);
        dto.setCourseId(1L);
        dto.setExamType("FINAL");
        dto.setScore(new BigDecimal("85.5"));
        dto.setSemester("2025-SPRING");
        
        Score entity = scoreConverter.toEntity(dto, student, course);
        
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(student, entity.getStudent());
        assertEquals(course, entity.getCourse());
        assertEquals("FINAL", entity.getExamType());
        assertEquals(0, new BigDecimal("85.5").compareTo(entity.getScore()));
        assertEquals("2025-SPRING", entity.getSemester());
        assertEquals(1, entity.getStatus());
    }

    @Test
    void testToEntityWithNullDTO() {
        Score entity = scoreConverter.toEntity(null, student, course);
        assertNull(entity);
    }
} 