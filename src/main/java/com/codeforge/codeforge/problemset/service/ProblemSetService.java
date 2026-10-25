package com.codeforge.codeforge.problemset.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.problemset.dto.ProblemSetDetailVO;
import com.codeforge.codeforge.problemset.dto.ProblemSetRequest;
import com.codeforge.codeforge.problemset.dto.ProblemSetVO;
import com.codeforge.codeforge.problemset.entity.ProblemSet;

public interface ProblemSetService extends IService<ProblemSet> {
    ProblemSetVO createProblemSet(ProblemSetRequest request);
    Page<ProblemSetVO> listProblemSets(Integer page, Integer size);
    ProblemSetDetailVO getProblemSetDetail(Long id);
    ProblemSetVO updateProblemSet(Long id, ProblemSetRequest request);
    void deleteProblemSet(Long id);
}