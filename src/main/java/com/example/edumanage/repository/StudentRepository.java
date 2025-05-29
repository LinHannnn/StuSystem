package com.example.edumanage.repository;

import com.example.edumanage.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    boolean existsByStudentId(String studentId);
    Optional<Student> findByStudentId(String studentId);
    Page<Student> findByStudentNameContaining(String name, Pageable pageable);
    List<Student> findByGender(Integer gender);
    List<Student> findByClassId(String classId);
    void deleteByStudentIdIn(List<String> studentIds);
    Integer countByClassId(String classId);
    List<Student> findByStudentNameContaining(String studentName);

    @Query("SELECT s FROM Student s WHERE " +
            "s.studentName LIKE CONCAT('%', :studentName, '%')")
    List<Student> findByStudentNameContainingQuery(String studentName);
} 