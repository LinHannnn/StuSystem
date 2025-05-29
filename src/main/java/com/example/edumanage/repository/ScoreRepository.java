package com.example.edumanage.repository;

import com.example.edumanage.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    
    // 根据学生ID查询所有成绩
    List<Score> findByStudentIdAndStatusOrderBySemesterDesc(Long studentId, Integer status);
    
    // 根据学期查询所有成绩
    List<Score> findBySemesterAndStatusOrderByStudentIdAsc(String semester, Integer status);
    
    // 根据学生ID和学期查询成绩
    List<Score> findByStudentIdAndSemesterAndStatusOrderByCourseIdAsc(Long studentId, String semester, Integer status);
    
    // 根据学生ID、课程ID、考试类型和学期查询成绩
    Optional<Score> findByStudentIdAndCourseIdAndExamTypeAndSemesterAndStatus(
            Long studentId, Long courseId, String examType, String semester, Integer status);
    
    // 查询某门课程的所有成绩
    List<Score> findByCourseIdAndStatusOrderByStudentIdAsc(Long courseId, Integer status);
    
    // 查询某门课程在某学期的所有成绩
    List<Score> findByCourseIdAndSemesterAndStatusOrderByStudentIdAsc(Long courseId, String semester, Integer status);
    
    // 自定义查询：获取学生在某学期的平均成绩
    @Query("SELECT AVG(s.score) FROM Score s WHERE s.student.id = :studentId AND s.semester = :semester AND s.status = 1")
    Double getStudentAverageScoreBySemester(@Param("studentId") Long studentId, @Param("semester") String semester);
    
    // 自定义查询：获取课程在某学期的平均成绩
    @Query("SELECT AVG(s.score) FROM Score s WHERE s.course.id = :courseId AND s.semester = :semester AND s.status = 1")
    Double getCourseAverageScoreBySemester(@Param("courseId") Long courseId, @Param("semester") String semester);
} 