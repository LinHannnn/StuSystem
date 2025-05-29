package com.example.edumanage.repository;

import com.example.edumanage.model.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassInfo, String>, JpaSpecificationExecutor<ClassInfo> {
    List<ClassInfo> findByStatus(String status);
    
    @Query("SELECT c FROM ClassInfo c WHERE c.startDate >= :startDate AND c.endDate <= :endDate")
    List<ClassInfo> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    ClassInfo findByClassName(String className);
    
    /**
     * 根据班级名称模糊查询
     */
    List<ClassInfo> findByClassNameContaining(String className);
    
    /**
     * 根据年级模糊查询
     */
    List<ClassInfo> findByGradeContaining(String grade);
    
    /**
     * 根据教室模糊查询
     */
    List<ClassInfo> findByClassroomContaining(String classroom);
    
    /**
     * 根据班主任ID查询
     */
    List<ClassInfo> findByHeadTeacherId(Long headTeacherId);
} 