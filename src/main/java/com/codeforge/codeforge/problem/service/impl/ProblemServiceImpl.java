package com.codeforge.codeforge.problem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.problem.dto.ProblemCreateRequest;
import com.codeforge.codeforge.problem.dto.ProblemDetailVO;
import com.codeforge.codeforge.problem.dto.ProblemUpdateRequest;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.entity.Problem;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemMapper;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.problem.service.ProblemService;
import com.codeforge.codeforge.user.dto.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Page<ProblemVO> listProblems(Integer page, Integer size, String difficulty, String status) {
        //创建一个分页的容器
        Page<Problem> pageParm = new Page<>(page, size);
        //创建一个查询条件容器
        LambdaQueryWrapper<Problem> queryWrapper = new LambdaQueryWrapper<> ();
        //如果从线程中获取的role不是管理员，就加状态查询为published或者是传照的人是本人
        if (!"ADMIN".equals(UserContext.get().getRole())) {
            queryWrapper.and(w->w.eq(Problem::getStatus,"PUBLISHED")
                    .or()
                    .eq(Problem::getCreatorId,UserContext.get().getId()));
        }
        //添加查询条件（三参数）
        queryWrapper.eq(difficulty!=null,Problem::getDifficulty,difficulty);
        queryWrapper.eq(status!=null,Problem::getStatus,status);
        queryWrapper.orderByDesc(Problem::getId);
        //进行分页查询
        Page<Problem> result=page(pageParm, queryWrapper);
        //创建一个problemvo的类型的分页容器
        Page<ProblemVO> vopage=new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        //创建一个list数组
        List<ProblemVO> voList=new ArrayList<>();
        //通过加强for来把里面的数据拿出来，放进数组中
        for (Problem problem : result.getRecords()) {
            voList.add(toVO(problem));
        }
        vopage.setRecords(voList);
        return vopage;
    }

    @Override
    public ProblemDetailVO getProblemDetail(Long id) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }
        UserVO current = UserContext.get();

        boolean isPUBLISHED = "PUBLISHED".equals(problem.getStatus());
        boolean isCreator = problem.getCreatorId().equals(current.getId());
        boolean isadmin = "ADMIN".equals(current.getRole());

        if (!isPUBLISHED && !isCreator && !isadmin) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }


        LambdaQueryWrapper<ProblemVersion> wrapper = new LambdaQueryWrapper<ProblemVersion>().eq(ProblemVersion::getProblemId, id).orderByDesc(ProblemVersion::getVersion).last("limit 1");
        ProblemVersion version = problemVersionMapper.selectOne(wrapper);
        ProblemDetailVO vo = new ProblemDetailVO();
        vo.setId(problem.getId());
        vo.setTitle(problem.getTitle());
        vo.setDifficulty(problem.getDifficulty());
        vo.setTags(problem.getTags());
        vo.setStatus(problem.getStatus());
        vo.setCreatorId(problem.getCreatorId());
        vo.setCreateTime(problem.getCreateTime());
        vo.setUpdateTime(problem.getUpdateTime());
        vo.setVersion(version.getVersion());
        vo.setDescription(version.getDescription());
        vo.setInputFormat(version.getInputFormat());
        vo.setOutputFormat(version.getOutputFormat());
        vo.setTimeLimit(version.getTimeLimit());
        vo.setMemoryLimit(version.getMemoryLimit());
        vo.setTestCases(version.getTestCases());
        vo.setCreatedAt(version.getCreatedAt());
        return vo;
    }

    @Override
    @Transactional
    public ProblemVO updateProblem(Long id, ProblemUpdateRequest request) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }
         UserVO current = UserContext.get();
        boolean isowner = problem.getCreatorId().equals(current.getId());
        boolean isadmin = "ADMIN".equals(current.getRole());

        if (!isowner && !isadmin) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        LambdaQueryWrapper<ProblemVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProblemVersion::getProblemId, id)
                .orderByDesc(ProblemVersion::getVersion)
                .last( "limit 1" );
        ProblemVersion latest = problemVersionMapper.selectOne(wrapper);
        ProblemVersion version = new ProblemVersion();
        version.setProblemId(id);
        version.setVersion(latest.getVersion()+1);
        version.setDescription(request.getDescription());
        version.setInputFormat(request.getInputFormat());
        version.setOutputFormat(request.getOutputFormat());
        version.setTimeLimit(request.getTimeLimit());
        version.setMemoryLimit(request.getMemoryLimit());
        version.setTestCases(request.getTestCases());
        version.setCreatedBy(current.getId());
        problemVersionMapper.insert(version);

        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setTags(request.getTags());
        problem.setUpdateTime(LocalDateTime.now());
        updateById(problem);


        return toVO(problem);
    }

    @Override
    public void deleteProblem(Long id) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        UserVO current = UserContext.get();
        boolean isowner = problem.getCreatorId().equals(current.getId());
        boolean isadmin = "ADMIN".equals(current.getRole());
        if (!isowner && !isadmin) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        removeById(id);

    }

    private ProblemVO toVO(Problem problem) {
        ProblemVO vo = new ProblemVO ();
        vo.setId(problem.getId());
        vo.setTitle(problem.getTitle());
        vo.setDifficulty(problem.getDifficulty());
        vo.setTags(problem.getTags());
        vo.setStatus(problem.getStatus());
        vo.setCreatorId(problem.getCreatorId());
        vo.setCreateTime(problem.getCreateTime());
        vo.setUpdateTime(problem.getUpdateTime());
        return vo;
    }
}
