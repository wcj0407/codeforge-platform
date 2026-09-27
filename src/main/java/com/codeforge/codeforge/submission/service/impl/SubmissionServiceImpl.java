package com.codeforge.codeforge.submission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.problem.entity.Problem;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.problem.service.ProblemService;
import com.codeforge.codeforge.submission.dto.SubmissionCreateRequest;
import com.codeforge.codeforge.submission.dto.SubmissionDetailVO;
import com.codeforge.codeforge.submission.dto.SubmissionVO;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import com.codeforge.codeforge.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {
    private final ProblemService problemService;
    private final ProblemVersionMapper problemVersionMapper;


    @Override
    public SubmissionVO createSubmission(SubmissionCreateRequest request) {
        //先根据id来查询数据库中有没有这个题目的信息
        Problem problem = problemService.getById(request.getProblemId());
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }
        //再查询最新版本信息
        ProblemVersion version = problemVersionMapper.selectOne(new LambdaQueryWrapper<ProblemVersion>()
                .eq(ProblemVersion::getProblemId, request.getProblemId())
                .orderByDesc(ProblemVersion::getVersion).last("limit 1"));

        //保存提交到数据库
        Submission submission = new Submission();
        submission.setProblemId(request.getProblemId());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setProblemVersion(version.getVersion());
        submission.setUserId(UserContext.get().getId());
        submission.setStatus("PENDING");
        save(submission);
        //转换成给前端看的数据
        return toVO(submission);
    }

    @Override
    public Page<SubmissionVO> listSubmission(Integer page, Integer size, String status) {
        Page<Submission> pap = new Page<>(page, size);
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Submission::getUserId, UserContext.get().getId());
        wrapper.eq(status!=null, Submission::getStatus, status);
        wrapper.orderByDesc(Submission::getId);

        Page<Submission> paged = page(pap, wrapper);

        Page<SubmissionVO> pagevo= new Page<>(paged.getCurrent(), paged.getSize(), paged.getTotal());
        List<SubmissionVO> list = new ArrayList<>();
        for (Submission submission : paged.getRecords()) {
            list.add(toVO(submission));
        }
        pagevo.setRecords(list);
        return pagevo;
    }

    @Override
    public SubmissionDetailVO getSubmission(Long id) {
        Submission submission = getById(id);
        if (submission == null) {
            throw new BusinessException(ErrorCode.SUBMISSION_NOT_FOUND);
        }
        if (!submission.getUserId().equals(UserContext.get().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }


        return toVODetail(submission);
    }

    private SubmissionDetailVO toVODetail(Submission submission) {
        SubmissionDetailVO submissionDetailVO = new SubmissionDetailVO();
        submissionDetailVO.setId(submission.getId());
        submissionDetailVO.setProblemId(submission.getProblemId());
        submissionDetailVO.setUserId(submission.getUserId());
        submissionDetailVO.setCode(submission.getCode());
        submissionDetailVO.setLanguage(submission.getLanguage());
        submissionDetailVO.setProblemVersion(submission.getProblemVersion());
        submissionDetailVO.setStatus(submission.getStatus());
        submissionDetailVO.setScore(submission.getScore());
        submissionDetailVO.setTimeUsed(submission.getTimeUsed());
        submissionDetailVO.setMemoryUsed(submission.getMemoryUsed());
        submissionDetailVO.setErrorMessage(submission.getErrorMessage());
        submissionDetailVO.setSubmitTime(submission.getSubmitTime());
        submissionDetailVO.setJudgeTime(submission.getJudgeTime());
        return submissionDetailVO;

    }

    private SubmissionVO toVO(Submission submission) {
        SubmissionVO vo = new SubmissionVO();
        vo.setId(submission.getId());
        vo.setProblemId(submission.getProblemId());
        vo.setProblemVersion(submission.getProblemVersion());
        vo.setUserId(submission.getUserId());
        vo.setLanguage(submission.getLanguage());
        vo.setStatus(submission.getStatus());
        vo.setScore(submission.getScore());
        vo.setTimeUsed(submission.getTimeUsed());
        vo.setMemoryUsed(submission.getMemoryUsed());
        vo.setJudgeTime(submission.getJudgeTime());
        vo.setSubmitTime(submission.getSubmitTime());
        return vo;

    }
}
