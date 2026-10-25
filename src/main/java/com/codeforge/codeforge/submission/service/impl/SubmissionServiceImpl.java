package com.codeforge.codeforge.submission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.outbox.entity.OutboxEvent;
import com.codeforge.codeforge.outbox.mapper.OutboxEventMapper;
import com.codeforge.codeforge.problem.constant.ProblemStatus;
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
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.entity.User;
import com.codeforge.codeforge.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {
    private final ProblemService problemService;
    private final ProblemVersionMapper problemVersionMapper;
    private final OutboxEventMapper outboxEventMapper;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public SubmissionVO createSubmission(SubmissionCreateRequest request) {
        //先根据id来查询数据库中有没有这个题目的信息
        Problem problem = problemService.getById(request.getProblemId());
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }
        // 只有已发布的题目才能提交（下架/草稿/待审核/驳回的题一律拒绝）
        if (!ProblemStatus.PUBLISHED.equals(problem.getStatus())) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_PUBLISHED);
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

        OutboxEvent outbox = new OutboxEvent();
        outbox.setEventType("JUDGE_SUBMISSION");
        outbox.setPayload("{\"submissionId\":"+submission.getId()+"}");
        outbox.setStatus("PENDING");
        outbox.setRetryCount(0);
        outboxEventMapper.insert(outbox);
        SubmissionVO vo = toVO(submission);
        vo.setProblemTitle(problem.getTitle());
        return vo;
    }

    @Override
    public Page<SubmissionVO> listSubmission(Integer page, Integer size, String status, Long userId) {
        UserVO current = UserContext.get();
        boolean isAdmin = "ADMIN".equals(current.getRole());

        Page<Submission> pap = new Page<>(page, size);
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
        if (isAdmin) {
            // 管理员可查看全量提交，也可按某个用户过滤
            wrapper.eq(userId != null, Submission::getUserId, userId);
        } else {
            // 普通用户只能看自己的，忽略传入的 userId，避免越权
            wrapper.eq(Submission::getUserId, current.getId());
        }
        wrapper.eq(status!=null, Submission::getStatus, status);
        wrapper.orderByDesc(Submission::getId);

        Page<Submission> paged = page(pap, wrapper);

        Page<SubmissionVO> pagevo= new Page<>(paged.getCurrent(), paged.getSize(), paged.getTotal());
        List<SubmissionVO> list = new ArrayList<>();

        // 批量查本页题目的名称，避免逐行查库（N+1）
        Map<Long, String> titleMap = new HashMap<>();
        List<Long> problemIds = new ArrayList<>();
        for (Submission s : paged.getRecords()) {
            if (!problemIds.contains(s.getProblemId())) {
                problemIds.add(s.getProblemId());
            }
        }
        if (!problemIds.isEmpty()) {
            for (Problem p : problemService.listByIds(problemIds)) {
                titleMap.put(p.getId(), p.getTitle());
            }
        }

        // 管理员列表额外展示提交人用户名（同样批量查询，避免 N+1）
        Map<Long, String> usernameMap = new HashMap<>();
        if (isAdmin) {
            List<Long> userIds = new ArrayList<>();
            for (Submission s : paged.getRecords()) {
                if (!userIds.contains(s.getUserId())) {
                    userIds.add(s.getUserId());
                }
            }
            if (!userIds.isEmpty()) {
                for (User u : userMapper.selectBatchIds(userIds)) {
                    usernameMap.put(u.getId(), u.getUsername());
                }
            }
        }

        for (Submission submission : paged.getRecords()) {
            SubmissionVO vo = toVO(submission);
            vo.setProblemTitle(titleMap.get(submission.getProblemId()));
            vo.setUsername(usernameMap.get(submission.getUserId()));
            list.add(vo);
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
        UserVO current = UserContext.get();
        boolean isOwner = submission.getUserId().equals(current.getId());
        boolean isAdmin = "ADMIN".equals(current.getRole());
        if (!isOwner && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        SubmissionDetailVO vo = toVODetail(submission);
        Problem problem = problemService.getById(submission.getProblemId());
        vo.setProblemTitle(problem != null ? problem.getTitle() : null);
        User owner = userMapper.selectById(submission.getUserId());
        vo.setUsername(owner != null ? owner.getUsername() : null);
        return vo;
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
