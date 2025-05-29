package com.example.edumanage.controller;

import com.example.edumanage.common.Result;
import com.example.edumanage.dto.ScoreDTO;
import com.example.edumanage.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "成绩管理", description = "成绩相关接口")
@RestController
@RequestMapping("/api/scores")
public class ScoreController {
    private static final Logger logger = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    private ScoreService scoreService;

    @Operation(summary = "创建成绩")
    @PostMapping
    public Result<ScoreDTO> createScore(@Valid @RequestBody ScoreDTO scoreDTO) {
        try {
            ScoreDTO createdScore = scoreService.createScore(scoreDTO);
            return Result.success(createdScore);
        } catch (Exception e) {
            logger.error("创建成绩失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "更新成绩")
    @PutMapping("/{id}")
    public Result<ScoreDTO> updateScore(@PathVariable Long id, @Valid @RequestBody ScoreDTO scoreDTO) {
        try {
            ScoreDTO updatedScore = scoreService.updateScore(id, scoreDTO);
            return Result.success(updatedScore);
        } catch (Exception e) {
            logger.error("更新成绩失败，ID: " + id, e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "根据ID获取成绩")
    @GetMapping("/{id}")
    public Result<ScoreDTO> getScoreById(@PathVariable Long id) {
        try {
            ScoreDTO score = scoreService.getScoreById(id);
            return Result.success(score);
        } catch (Exception e) {
            logger.error("获取成绩失败，ID: " + id, e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取所有成绩（分页）")
    @GetMapping("/page")
    public Result<Page<ScoreDTO>> getAllScores(Pageable pageable) {
        try {
            Page<ScoreDTO> scores = scoreService.getAllScores(pageable);
            return Result.success(scores);
        } catch (Exception e) {
            logger.error("获取成绩分页列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取所有成绩")
    @GetMapping
    public Result<List<ScoreDTO>> getAllScores() {
        try {
            List<ScoreDTO> scores = scoreService.getAllScores();
            return Result.success(scores);
        } catch (Exception e) {
            logger.error("获取成绩列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "删除成绩")
    @DeleteMapping("/{id}")
    public Result<Void> deleteScore(@PathVariable Long id) {
        try {
            scoreService.deleteScore(id);
            return Result.success();
        } catch (Exception e) {
            logger.error("删除成绩失败，ID: " + id, e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "批量删除成绩")
    @DeleteMapping("/batch")
    public Result<Void> deleteScores(@RequestBody List<Long> ids) {
        try {
            scoreService.deleteScores(ids);
            return Result.success();
        } catch (Exception e) {
            logger.error("批量删除成绩失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "根据学生ID获取成绩")
    @GetMapping("/student/{studentId}")
    public Result<List<ScoreDTO>> getScoresByStudentId(@PathVariable Long studentId) {
        try {
            List<ScoreDTO> scores = scoreService.getScoresByStudentId(studentId);
            return Result.success(scores);
        } catch (Exception e) {
            logger.error("获取学生成绩失败，学生ID: " + studentId, e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "根据学期获取所有成绩")
    @GetMapping("/semester/{semester}")
    public Result<List<ScoreDTO>> getScoresBySemester(@PathVariable String semester) {
        try {
            List<ScoreDTO> scores = scoreService.getScoresBySemester(semester);
            return Result.success(scores);
        } catch (Exception e) {
            logger.error("获取学期成绩失败，学期: " + semester, e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "根据学生ID和学期获取成绩")
    @GetMapping("/student/{studentId}/semester/{semester}")
    public Result<List<ScoreDTO>> getScoresByStudentIdAndSemester(
            @PathVariable Long studentId, 
            @PathVariable String semester) {
        try {
            List<ScoreDTO> scores = scoreService.getScoresByStudentIdAndSemester(studentId, semester);
            return Result.success(scores);
        } catch (Exception e) {
            logger.error("获取学生学期成绩失败，学生ID: " + studentId + ", 学期: " + semester, e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取学生在某学期的平均成绩")
    @GetMapping("/student/{studentId}/semester/{semester}/average")
    public Result<Map<String, Double>> getStudentAverageScoreBySemester(
            @PathVariable Long studentId, 
            @PathVariable String semester) {
        try {
            Double averageScore = scoreService.getStudentAverageScoreBySemester(studentId, semester);
            return Result.success(Map.of("averageScore", averageScore != null ? averageScore : 0.0));
        } catch (Exception e) {
            logger.error("获取学生平均成绩失败，学生ID: " + studentId + ", 学期: " + semester, e);
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取课程在某学期的平均成绩")
    @GetMapping("/course/{courseId}/semester/{semester}/average")
    public Result<Map<String, Double>> getCourseAverageScoreBySemester(
            @PathVariable Long courseId, 
            @PathVariable String semester) {
        try {
            Double averageScore = scoreService.getCourseAverageScoreBySemester(courseId, semester);
            return Result.success(Map.of("averageScore", averageScore != null ? averageScore : 0.0));
        } catch (Exception e) {
            logger.error("获取课程平均成绩失败，课程ID: " + courseId + ", 学期: " + semester, e);
            return Result.fail(e.getMessage());
        }
    }
} 