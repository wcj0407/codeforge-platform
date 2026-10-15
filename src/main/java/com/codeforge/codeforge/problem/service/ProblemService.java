package com.codeforge.codeforge.problem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.problem.dto.ProblemCreateRequest;
import com.codeforge.codeforge.problem.dto.ProblemDetailVO;
import com.codeforge.codeforge.problem.dto.ProblemUpdateRequest;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.entity.Problem;

public interface ProblemService extends IService<Problem> {
    ProblemVO createProblem(ProblemCreateRequest request);

    Page<ProblemVO> listProblems(Integer page, Integer size, String difficulty, String status);

    ProblemDetailVO getProblemDetail(Long id);

    ProblemVO updateProblem (Long id, ProblemUpdateRequest request) ;

    ProblemVO updateStatus(Long id, String status);

    void deleteProblem(Long id);
}
