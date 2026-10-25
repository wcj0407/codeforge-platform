package com.codeforge.codeforge.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.common.annotation.RequireRole;
import com.codeforge.codeforge.exam.dto.ExamDetailVO;
import com.codeforge.codeforge.exam.dto.ExamRequest;
import com.codeforge.codeforge.exam.dto.ExamVO;
import com.codeforge.codeforge.exam.dto.RankItemVO;
import com.codeforge.codeforge.exam.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exam")
public class ExamController {
    private final ExamService examService;

    @RequireRole({"ADMIN"})
    @PostMapping
    public Result<ExamVO> createExam(@Valid @RequestBody ExamRequest request) {
        return Result.success(examService.createExam(request));
    }

    @GetMapping
    public Result<Page<ExamVO>> listExams(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(examService.listExams(page, size));
    }

    @GetMapping("/{id}")
    public Result<ExamDetailVO> getExam(@PathVariable Long id) {
        return Result.success(examService.getExamDetail(id));
    }

    @GetMapping("/{id}/rank")
    public Result<List<RankItemVO>> getRank(@PathVariable Long id) {
        return Result.success(examService.getRank(id));
    }

    @RequireRole({"ADMIN"})
    @PutMapping("/{id}")
    public Result<ExamVO> updateExam(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return Result.success(examService.updateExam(id, request));
    }

    @RequireRole({"ADMIN"})
    @DeleteMapping("/{id}")
    public Result<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return Result.success();
    }
}