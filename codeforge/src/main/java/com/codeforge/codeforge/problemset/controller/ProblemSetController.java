package com.codeforge.codeforge.problemset.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.problemset.dto.ProblemSetDetailVO;
import com.codeforge.codeforge.problemset.dto.ProblemSetRequest;
import com.codeforge.codeforge.problemset.dto.ProblemSetVO;
import com.codeforge.codeforge.problemset.service.ProblemSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problem-set")
public class ProblemSetController {
    private final ProblemSetService problemSetService;

    @PostMapping
    public Result<ProblemSetVO> createProblemSet(@Valid @RequestBody ProblemSetRequest request) {
        return Result.success(problemSetService.createProblemSet(request));
    }

    @GetMapping
    public Result<Page<ProblemSetVO>> listProblemSets(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(problemSetService.listProblemSets(page, size));
    }

    @GetMapping("/{id}")
    public Result<ProblemSetDetailVO> getProblemSet(@PathVariable Long id) {
        return Result.success(problemSetService.getProblemSetDetail(id));
    }

    @PutMapping("/{id}")
    public Result<ProblemSetVO> updateProblemSet(@PathVariable Long id,
                                                 @Valid @RequestBody ProblemSetRequest request) {
        return Result.success(problemSetService.updateProblemSet(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProblemSet(@PathVariable Long id) {
        problemSetService.deleteProblemSet(id);
        return Result.success();
    }
}