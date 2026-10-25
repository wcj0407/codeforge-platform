package com.codeforge.codeforge.submission.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.submission.dto.SubmissionCreateRequest;
import com.codeforge.codeforge.submission.dto.SubmissionDetailVO;
import com.codeforge.codeforge.submission.dto.SubmissionVO;
import com.codeforge.codeforge.submission.entity.Submission;
import jakarta.validation.Valid;

public interface SubmissionService extends IService<Submission> {
    SubmissionVO createSubmission( SubmissionCreateRequest request);

    Page<SubmissionVO> listSubmission(Integer page, Integer size, String status, Long userId);

    SubmissionDetailVO getSubmission(Long id);
}
