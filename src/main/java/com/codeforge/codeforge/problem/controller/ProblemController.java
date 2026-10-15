package com.codeforge.codeforge.problem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.common.annotation.RequireRole;
import com.codeforge.codeforge.problem.dto.ProblemCreateRequest;
import com.codeforge.codeforge.problem.dto.ProblemDetailVO;
import com.codeforge.codeforge.problem.dto.ProblemStatusRequest;
import com.codeforge.codeforge.problem.dto.ProblemUpdateRequest;
import com.codeforge.codeforge.problem.dto.ProblemVO;
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
            @RequestParam(required = false) String status
    ) {
        return Result.success(problemService.listProblems(page,size,difficulty,status));

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
    @PutMapping("/{id}/status")
    public Result<ProblemVO> updateStatus(@PathVariable Long id, @Valid @RequestBody ProblemStatusRequest request) {
        return Result.success(problemService.updateStatus(id, request.getStatus()));
    }

    @RequireRole({"AUTHOR","ADMIN"})
    @DeleteMapping("/{id}")
    public Result<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return Result.success();
    }
}
