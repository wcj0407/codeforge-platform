package com.codeforge.codeforge.judge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.judge.dto.JudgeResult;
import com.codeforge.codeforge.judge.service.DockerJudgeExecutor;
import com.codeforge.codeforge.judge.service.JudgeService;
import com.codeforge.codeforge.problem.entity.ProblemTestCase;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemTestCaseMapper;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final SubmissionMapper submissionMapper;
    private final ProblemVersionMapper problemVersionMapper;
    private final ProblemTestCaseMapper problemTestCaseMapper;
    private final DockerJudgeExecutor dockerJudgeExecutor;

    @Override
    public JudgeResult judge(Long submissionId) {
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException(ErrorCode.SUBMISSION_NOT_FOUND);
        }

        // 幂等第一层：终态（已判完）的提交直接返回结果，避免 MQ 重复投递导致重复判题。
        // 只有 PENDING（首次消费）和 SYSTEM_ERROR（允许重试）会尝试抢占判题权。
        if (!"PENDING".equals(submission.getStatus()) && !"SYSTEM_ERROR".equals(submission.getStatus())) {
            return existingResult(submission);
        }

        // 幂等第二层：用条件更新原子抢占。并发重复消息只有一个能把状态改成 RUNNING，
        // 抢不到的消费者直接返回当前结果，不会重复启动 Docker 判题。
        int claimed = submissionMapper.claimForJudge(submissionId);
        if (claimed == 0) {
            Submission latest = submissionMapper.selectById(submissionId);
            return existingResult(latest != null ? latest : submission);
        }
        submission.setStatus("RUNNING");

        JudgeResult result = new JudgeResult();
        try {
            ProblemVersion version = problemVersionMapper.selectOne(new LambdaQueryWrapper<ProblemVersion>()
                    .eq(ProblemVersion::getProblemId, submission.getProblemId())
                    .eq(ProblemVersion::getVersion, submission.getProblemVersion()));
            if (version == null) {
                result.setStatus("SYSTEM_ERROR");
                result.setErrorMessage("题目版本不存在，无法判题");
                return result;
            }

            List<ProblemTestCase> testCases = problemTestCaseMapper.selectList(
                    new LambdaQueryWrapper<ProblemTestCase>()
                            .eq(ProblemTestCase::getProblemVersionId, version.getId())
                            .orderByAsc(ProblemTestCase::getSortNo));
            if (testCases.isEmpty()) {
                result.setStatus("SYSTEM_ERROR");
                result.setErrorMessage("该题目没有测试用例");
                return result;
            }

            result = dockerJudgeExecutor.execute(
                    submission.getCode(), testCases, version.getTimeLimit(), version.getMemoryLimit());
            return result;
        } catch (RuntimeException e) {
            // 执行器内部已标记 SYSTEM_ERROR；这里兜底，保证 finally 能回写并触发 MQ 重试
            result.setStatus("SYSTEM_ERROR");
            result.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            submission.setStatus(result.getStatus());
            submission.setScore(result.getScore());
            submission.setErrorMessage(result.getErrorMessage());
            submission.setTimeUsed(result.getTimeUsed());
            submission.setMemoryUsed(result.getMemoryUsed());
            submission.setJudgeTime(LocalDateTime.now());
            submissionMapper.updateById(submission);
        }
    }

    private JudgeResult existingResult(Submission submission) {
        JudgeResult result = new JudgeResult();
        result.setStatus(submission.getStatus());
        result.setScore(submission.getScore());
        result.setErrorMessage(submission.getErrorMessage());
        result.setTimeUsed(submission.getTimeUsed());
        result.setMemoryUsed(submission.getMemoryUsed());
        return result;
    }
}
