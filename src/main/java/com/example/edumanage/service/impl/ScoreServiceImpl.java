package com.example.edumanage.service.impl;

import com.example.edumanage.dto.ScoreConverter;
import com.example.edumanage.dto.ScoreDTO;
import com.example.edumanage.exception.ResourceNotFoundException;
import com.example.edumanage.model.Course;
import com.example.edumanage.model.Score;
import com.example.edumanage.model.Student;
import com.example.edumanage.repository.CourseRepository;
import com.example.edumanage.repository.ScoreRepository;
import com.example.edumanage.repository.StudentRepository;
import com.example.edumanage.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ScoreConverter scoreConverter;

    @Override
    @Transactional
    public ScoreDTO createScore(ScoreDTO scoreDTO) {
        // 查找学生
        Student student = studentRepository.findById(scoreDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在，ID: " + scoreDTO.getStudentId()));

        // 查找课程
        Course course = courseRepository.findById(scoreDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在，ID: " + scoreDTO.getCourseId()));

        // 检查是否已存在相同学生、课程、考试类型和学期的成绩
        Optional<Score> existingScore = scoreRepository.findByStudentIdAndCourseIdAndExamTypeAndSemesterAndStatus(
                student.getId(), course.getId(), scoreDTO.getExamType(), scoreDTO.getSemester(), 1);
        
        if (existingScore.isPresent()) {
            throw new IllegalArgumentException("该学生在此课程、考试类型和学期已有成绩记录");
        }

        // 转换为实体并保存
        Score score = scoreConverter.toEntity(scoreDTO, student, course);
        Score savedScore = scoreRepository.save(score);
        
        return scoreConverter.toDTO(savedScore);
    }

    @Override
    @Transactional
    public ScoreDTO updateScore(Long id, ScoreDTO scoreDTO) {
        // 查找现有成绩
        Score existingScore = scoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("成绩不存在，ID: " + id));

        // 查找学生
        Student student = studentRepository.findById(scoreDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在，ID: " + scoreDTO.getStudentId()));

        // 查找课程
        Course course = courseRepository.findById(scoreDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在，ID: " + scoreDTO.getCourseId()));

        // 如果学生、课程、考试类型或学期有变化，检查是否与其他记录冲突
        if (!existingScore.getStudent().getId().equals(student.getId()) ||
                !existingScore.getCourse().getId().equals(course.getId()) ||
                !existingScore.getExamType().equals(scoreDTO.getExamType()) ||
                !existingScore.getSemester().equals(scoreDTO.getSemester())) {
            
            Optional<Score> conflictingScore = scoreRepository.findByStudentIdAndCourseIdAndExamTypeAndSemesterAndStatus(
                    student.getId(), course.getId(), scoreDTO.getExamType(), scoreDTO.getSemester(), 1);
            
            if (conflictingScore.isPresent() && !conflictingScore.get().getId().equals(id)) {
                throw new IllegalArgumentException("该学生在此课程、考试类型和学期已有成绩记录");
            }
        }

        // 更新成绩信息
        existingScore.setStudent(student);
        existingScore.setCourse(course);
        existingScore.setExamType(scoreDTO.getExamType());
        existingScore.setScore(scoreDTO.getScore());
        existingScore.setSemester(scoreDTO.getSemester());
        if (scoreDTO.getStatus() != null) {
            existingScore.setStatus(scoreDTO.getStatus());
        }

        Score updatedScore = scoreRepository.save(existingScore);
        return scoreConverter.toDTO(updatedScore);
    }

    @Override
    public ScoreDTO getScoreById(Long id) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("成绩不存在，ID: " + id));
        return scoreConverter.toDTO(score);
    }

    @Override
    public Page<ScoreDTO> getAllScores(Pageable pageable) {
        return scoreRepository.findAll(pageable)
                .map(scoreConverter::toDTO);
    }

    @Override
    public List<ScoreDTO> getAllScores() {
        List<Score> scores = scoreRepository.findAll();
        return scoreConverter.toDTOList(scores);
    }

    @Override
    @Transactional
    public void deleteScore(Long id) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("成绩不存在，ID: " + id));
        score.setStatus(0);  // 软删除
        scoreRepository.save(score);
    }

    @Override
    @Transactional
    public void deleteScores(List<Long> ids) {
        List<Score> scores = scoreRepository.findAllById(ids);
        scores.forEach(score -> score.setStatus(0));  // 软删除
        scoreRepository.saveAll(scores);
    }

    @Override
    public List<ScoreDTO> getScoresByStudentId(Long studentId) {
        // 检查学生是否存在
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("学生不存在，ID: " + studentId);
        }
        
        List<Score> scores = scoreRepository.findByStudentIdAndStatusOrderBySemesterDesc(studentId, 1);
        return scoreConverter.toDTOList(scores);
    }

    @Override
    public List<ScoreDTO> getScoresByStudentName(String studentName) {
        // 根据学生姓名查找学生
        List<Student> students = studentRepository.findByStudentNameContaining(studentName);
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("未找到姓名包含 '" + studentName + "' 的学生");
        }
        
        // 获取所有匹配学生的成绩
        List<Score> allScores = new ArrayList<>();
        for (Student student : students) {
            List<Score> studentScores = scoreRepository.findByStudentIdAndStatusOrderBySemesterDesc(student.getId(), 1);
            allScores.addAll(studentScores);
        }
        
        return scoreConverter.toDTOList(allScores);
    }

    @Override
    public List<ScoreDTO> getScoresBySemester(String semester) {
        List<Score> scores = scoreRepository.findBySemesterAndStatusOrderByStudentIdAsc(semester, 1);
        return scoreConverter.toDTOList(scores);
    }

    @Override
    public List<ScoreDTO> getScoresByStudentIdAndSemester(Long studentId, String semester) {
        // 检查学生是否存在
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("学生不存在，ID: " + studentId);
        }
        
        List<Score> scores = scoreRepository.findByStudentIdAndSemesterAndStatusOrderByCourseIdAsc(studentId, semester, 1);
        return scoreConverter.toDTOList(scores);
    }

    @Override
    public List<ScoreDTO> getScoresByStudentNameAndSemester(String studentName, String semester) {
        // 根据学生姓名查找学生
        List<Student> students = studentRepository.findByStudentNameContaining(studentName);
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("未找到姓名包含 '" + studentName + "' 的学生");
        }
        
        // 获取所有匹配学生在指定学期的成绩
        List<Score> allScores = new ArrayList<>();
        for (Student student : students) {
            List<Score> studentScores = scoreRepository.findByStudentIdAndSemesterAndStatusOrderByCourseIdAsc(
                    student.getId(), semester, 1);
            allScores.addAll(studentScores);
        }
        
        return scoreConverter.toDTOList(allScores);
    }

    @Override
    public Double getStudentAverageScoreBySemester(Long studentId, String semester) {
        // 检查学生是否存在
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("学生不存在，ID: " + studentId);
        }
        
        return scoreRepository.getStudentAverageScoreBySemester(studentId, semester);
    }

    @Override
    public Double getCourseAverageScoreBySemester(Long courseId, String semester) {
        // 检查课程是否存在
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("课程不存在，ID: " + courseId);
        }
        
        return scoreRepository.getCourseAverageScoreBySemester(courseId, semester);
    }
} 