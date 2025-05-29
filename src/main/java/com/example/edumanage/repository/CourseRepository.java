package com.example.edumanage.repository;

import com.example.edumanage.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    /**
     * 根据课程名称查询课程
     */
    List<Course> findByCourseNameContaining(String courseName);
    
    /**
     * 根据班级ID查询课程
     */
    List<Course> findByClassInfoClassId(String classId);
    
    /**
     * 根据教师ID查询课程
     */
    List<Course> findByTeacherId(Long teacherId);
    
    /**
     * 根据教师名称查询课程
     */
    @Query("SELECT c FROM Course c WHERE c.teacher.name LIKE %:teacherName%")
    List<Course> findByTeacherName(@Param("teacherName") String teacherName);
    
    /**
     * 查询指定班级和教师的课程
     */
    List<Course> findByClassInfoClassIdAndTeacherId(String classId, Long teacherId);
    
    /**
     * 根据星期几查询课程
     */
    List<Course> findByWeekDay(Integer weekDay);
    
    /**
     * 根据课程时间段查询课程
     */
    List<Course> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalTime endTime, LocalTime startTime);
} 