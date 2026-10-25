package com.codeforge.codeforge.judge.service;

import com.codeforge.codeforge.judge.dto.JudgeResult;

public interface JudgeService {
    JudgeResult judge(Long submissionId);
}