package com.codeforge.codeforge;

import com.codeforge.codeforge.judge.dto.JudgeResult;
import com.codeforge.codeforge.judge.service.JudgeService;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JudgeServiceTest {

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Test
    public void testJudge() {
        JudgeResult result = judgeService.judge(1L);
        System.out.println("判题结果: " + result.getStatus());

        Submission submission = submissionMapper.selectById(1L);
        System.out.println("数据库状态: " + submission.getStatus());
    }

    @Test public void testJudgeTimeout () {
        JudgeResult result = judgeService.judge( 2L );
        System.out.println( "判题结果: " + result.getStatus());
        Submission submission = submissionMapper.selectById( 2L );
        System.out.println( "数据库状态: " + submission.getStatus());
    }

    @Test public void testJudgeMemoryBomb () {
        JudgeResult result = judgeService.judge( 3L );
        System.out.println( "判题结果: " + result.getStatus());
        Submission submission = submissionMapper.selectById( 3L );
        System.out.println( "数据库状态: " + submission.getStatus());
        System.out.println( "错误信息: " + submission.getErrorMessage());
    }
}