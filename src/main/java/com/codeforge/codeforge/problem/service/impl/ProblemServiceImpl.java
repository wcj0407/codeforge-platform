package com.codeforge.codeforge.problem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.problem.dto.ProblemCreateRequest;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.entity.Problem;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemMapper;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {
    private final ProblemMapper problemMapper;
    private final ProblemVersionMapper problemVersionMapper;
    @Override
    @Transactional
    public ProblemVO createProblem(ProblemCreateRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setTags(request.getTags());
        problem.setStatus( "DRAFT" );
        problem.setCreatorId(UserContext.get().getId());
        save(problem);

        ProblemVersion version = new ProblemVersion ();
        version.setProblemId(problem.getId());
        version.setVersion( 1 );
        version.setDescription(request.getDescription());
        version.setInputFormat(request.getInputFormat());
        version.setOutputFormat(request.getOutputFormat());
        version.setTimeLimit(request.getTimeLimit());
        version.setMemoryLimit(request.getMemoryLimit());
        version.setTestCases(request.getTestCases());
        version.setCreatedBy(UserContext.get().getId());
        problemVersionMapper.insert(version);

        ProblemVO vo = new ProblemVO ();
        vo.setId(problem.getId());
        vo.setTitle(problem.getTitle());
        vo.setDifficulty(problem.getDifficulty());
        vo.setTags(problem.getTags());
        vo.setStatus(problem.getStatus());
        vo.setCreatorId(problem.getCreatorId());
        return vo;
    }
}
