package com.codeforge.codeforge.submission.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.submission.dto.SubmissionCreateRequest;
import com.codeforge.codeforge.submission.dto.SubmissionDetailVO;
import com.codeforge.codeforge.submission.dto.SubmissionVO;
import com.codeforge.codeforge.submission.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submission")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    @PostMapping
    public Result<SubmissionVO> createSubmission(@Valid @RequestBody SubmissionCreateRequest request) {
        return Result.success(submissionService.createSubmission(request));
    }

    @GetMapping
    public Result<Page<SubmissionVO>> listSubmission(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status
    ) {
        return Result.success(submissionService.listSubmission(page,size,status));
    }

    @GetMapping("/{id}")
    public Result<SubmissionDetailVO> getSubmission(@PathVariable Long id) {
        return Result.success(submissionService.getSubmission(id));
    }
}
