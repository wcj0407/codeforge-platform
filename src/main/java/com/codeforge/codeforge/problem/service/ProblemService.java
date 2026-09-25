package com.codeforge.codeforge.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.problem.dto.ProblemCreateRequest;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.entity.Problem;

public interface ProblemService extends IService<Problem> {
    ProblemVO createProblem(ProblemCreateRequest request);
}
