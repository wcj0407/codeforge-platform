package com.codeforge.codeforge.problem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.common.annotation.RequireRole;
import com.codeforge.codeforge.problem.dto.ProblemCreateRequest;
import com.codeforge.codeforge.problem.dto.ProblemDetailVO;
import com.codeforge.codeforge.problem.dto.ProblemUpdateRequest;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.dto.ReviewRequest;
import com.codeforge.codeforge.problem.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problem")
public class ProblemController {
    private final ProblemService problemService;
    @RequireRole({"AUTHOR", "ADMIN"})
    @PostMapping
    public Result<ProblemVO> createProblem(@Valid @RequestBody ProblemCreateRequest request) {
        return Result.success(problemService.createProblem(request));
    }

    @GetMapping
    public Result<Page<ProblemVO>> listProblems(
            @RequestParam (defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(problemService.listProblems(page,size,difficulty,status,keyword));

    }

    @RequireRole({"AUTHOR","ADMIN"})
    @GetMapping("/my")
    public Result<Page<ProblemVO>> myProblems(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.success(problemService.listMyProblems(page, size));
    }

    @GetMapping("/{id}")
    public Result<ProblemDetailVO> getProblem(@PathVariable Long id) {
        return Result.success(problemService.getProblemDetail(id));
    }

    @RequireRole({"AUTHOR","ADMIN"})
    @PutMapping("/{id}")
    public Result<ProblemVO> updateProblem(@PathVariable Long id, @Valid @RequestBody ProblemUpdateRequest request) {
        return Result.success(problemService.updateProblem(id,request));
    }

    @RequireRole({"AUTHOR","ADMIN"})
    @PostMapping("/{id}/submit-review")
    public Result<ProblemVO> submitReview(@PathVariable Long id) {
        return Result.success(problemService.submitForReview(id));
    }

    @RequireRole({"ADMIN"})
    @PostMapping("/{id}/review")
    public Result<ProblemVO> reviewProblem(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return Result.success(problemService.reviewProblem(id, request));
    }

    @RequireRole({"AUTHOR","ADMIN"})
    @PostMapping("/{id}/offline")
    public Result<ProblemVO> offline(@PathVariable Long id) {

        return Result.success(problemService.offline(id));
    }

    @RequireRole({"AUTHOR","ADMIN"})
    @DeleteMapping("/{id}")
    public Result<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return Result.success();
    }
}
