package com.codeforge.codeforge.judge;

import com.codeforge.codeforge.judge.dto.JudgeResult;
import com.codeforge.codeforge.judge.service.DockerJudgeExecutor;
import com.codeforge.codeforge.judge.service.impl.JudgeServiceImpl;
import com.codeforge.codeforge.problem.entity.ProblemTestCase;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemTestCaseMapper;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * RabbitMQ 重复投递下的判题幂等测试：
 * 已进入终态的提交再次收到消息时，不应重新执行 Docker 判题。
 */
@ExtendWith(MockitoExtension.class)
class JudgeIdempotencyTest {

    @Mock
    private SubmissionMapper submissionMapper;
    @Mock
    private ProblemVersionMapper problemVersionMapper;
    @Mock
    private ProblemTestCaseMapper problemTestCaseMapper;
    @Mock
    private DockerJudgeExecutor dockerJudgeExecutor;
    @InjectMocks
    private JudgeServiceImpl judgeService;

    @Test
    void duplicateMessageDoesNotJudgeAcceptedSubmissionAgain() {
        Submission submission = submission(1L, "ACCEPTED");
        submission.setScore(100);
        submission.setTimeUsed(12);
        submission.setMemoryUsed(2048);
        when(submissionMapper.selectById(1L)).thenReturn(submission);

        JudgeResult result = judgeService.judge(1L);

        assertEquals("ACCEPTED", result.getStatus());
        assertEquals(100, result.getScore());
        assertEquals(12, result.getTimeUsed());
        assertEquals(2048, result.getMemoryUsed());
        // 关键断言：没有重新编译/运行，也没有再写库
        verifyNoInteractions(problemVersionMapper, problemTestCaseMapper, dockerJudgeExecutor);
        verify(submissionMapper, never()).updateById(any(Submission.class));
    }

    @Test
    void duplicateMessageDoesNotJudgeWrongAnswerAgain() {
        Submission submission = submission(2L, "WRONG_ANSWER");
        submission.setScore(0);
        when(submissionMapper.selectById(2L)).thenReturn(submission);

        JudgeResult result = judgeService.judge(2L);

        assertEquals("WRONG_ANSWER", result.getStatus());
        verifyNoInteractions(dockerJudgeExecutor);
        verify(submissionMapper, never()).updateById(any(Submission.class));
    }

    @Test
    void systemErrorSubmissionIsAllowedToRetry() {
        Submission submission = submission(3L, "SYSTEM_ERROR");
        when(submissionMapper.selectById(3L)).thenReturn(submission);
        when(submissionMapper.claimForJudge(3L)).thenReturn(1);
        when(problemVersionMapper.selectOne(any())).thenReturn(null);

        JudgeResult result = judgeService.judge(3L);

        assertEquals("SYSTEM_ERROR", result.getStatus());
        // 允许重试：抢占成功后会再次写回状态；但版本缺失时不会进入 Docker
        verify(submissionMapper, org.mockito.Mockito.atLeastOnce()).updateById(any(Submission.class));
        verifyNoInteractions(dockerJudgeExecutor);
    }

    @Test
    void concurrentDuplicateDoesNotJudgeWhenClaimFails() {
        Submission pending = submission(4L, "PENDING");
        Submission running = submission(4L, "RUNNING");
        // 第一次读到 PENDING，抢占失败后重读已经是 RUNNING
        when(submissionMapper.selectById(4L)).thenReturn(pending, running);
        when(submissionMapper.claimForJudge(4L)).thenReturn(0);

        JudgeResult result = judgeService.judge(4L);

        assertEquals("RUNNING", result.getStatus());
        verifyNoInteractions(problemVersionMapper, problemTestCaseMapper, dockerJudgeExecutor);
        verify(submissionMapper, never()).updateById(any(Submission.class));
    }

    @Test
    void duplicateMessageSequenceOnlyRunsDockerOnce() {
        // 模拟 MQ 把同一条消息投递两次：第一次正常判完，第二次应直接命中终态
        Submission submission = submission(5L, "PENDING");
        when(submissionMapper.selectById(5L)).thenReturn(submission);
        when(submissionMapper.claimForJudge(5L)).thenReturn(1);

        ProblemVersion version = new ProblemVersion();
        version.setId(99L);
        version.setTimeLimit(1000);
        version.setMemoryLimit(256);
        when(problemVersionMapper.selectOne(any())).thenReturn(version);

        ProblemTestCase testCase = new ProblemTestCase();
        testCase.setInput("1 2");
        testCase.setOutput("3");
        testCase.setIsPublic(1);
        when(problemTestCaseMapper.selectList(any())).thenReturn(List.of(testCase));

        JudgeResult accepted = new JudgeResult();
        accepted.setStatus("ACCEPTED");
        accepted.setScore(100);
        accepted.setTimeUsed(10);
        accepted.setMemoryUsed(1024);
        when(dockerJudgeExecutor.execute(anyString(), anyList(), eq(1000), eq(256)))
                .thenReturn(accepted);

        JudgeResult first = judgeService.judge(5L);
        JudgeResult second = judgeService.judge(5L);

        assertEquals("ACCEPTED", first.getStatus());
        assertEquals("ACCEPTED", second.getStatus());
        // 两次投递只编译/运行一次，也只写一次最终结果
        verify(dockerJudgeExecutor, times(1)).execute(anyString(), anyList(), eq(1000), eq(256));
        verify(submissionMapper, times(1)).claimForJudge(5L);
        verify(submissionMapper, times(1)).updateById(any(Submission.class));
    }

    private Submission submission(Long id, String status) {
        Submission submission = new Submission();
        submission.setId(id);
        submission.setStatus(status);
        submission.setProblemId(100L);
        submission.setProblemVersion(1);
        submission.setCode("public class Solution {}");
        return submission;
    }
}
