package com.codeforge.codeforge.judge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeforge.codeforge.judge.dto.JudgeResult;
import com.codeforge.codeforge.judge.service.JudgeService;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {
    private final SubmissionMapper submissionMapper;
    private final ProblemVersionMapper problemVersionMapper;

    @Override
    public JudgeResult judge(Long submissionId) {
        // 1. 查 submission
        Submission submission = submissionMapper.selectById(submissionId);

        // 2. 更新状态为 RUNNING
        submission.setStatus("RUNNING");
        submissionMapper.updateById(submission);

        // 3. 查 problem_version（按题目 + 版本精确定位）
        ProblemVersion version = problemVersionMapper.selectOne(new LambdaQueryWrapper<ProblemVersion>()
                .eq(ProblemVersion::getProblemId, submission.getProblemId())
                .eq(ProblemVersion::getVersion, submission.getProblemVersion()));
        // 4. 建临时目录（在 try 内创建，finally 统一清理）
        Path tempDirectory = null;
        JudgeResult result = new JudgeResult();
        int passedCases = 0;
        int totalCases = version.getTestCases().size();

        try {
            tempDirectory = Files.createTempDirectory("judge-");
            // 4. 写 Solution.java
            Path javaFile = tempDirectory.resolve("Solution.java");
            Files.writeString(javaFile, submission.getCode());

            // 5. javac 编译
            ProcessBuilder compileBuilder = new ProcessBuilder("docker","run","--rm"
                    ,"--network","none","-v",tempDirectory+":/sandbox","codeforge-judge:1.0","javac","Solution.java");
            compileBuilder.redirectErrorStream(true);
            Process compileProcess = compileBuilder.start();

            StringBuilder compileOutput = new StringBuilder();
            BufferedReader compileReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
            String line;
            while ((line = compileReader.readLine()) != null) {
                compileOutput.append(line).append("\n");
            }
            compileProcess.waitFor(10, TimeUnit.SECONDS);
            if (compileProcess.exitValue() != 0) {
                result.setStatus("COMPILE_ERROR");
                result.setErrorMessage(compileOutput.toString());
                return result;
            }

            // 6. 遍历测试用例：执行 + 喂输入 + 比对
            for (ProblemVersion.TestCase testCase : version.getTestCases()) {
                ProcessBuilder runBuilder = new ProcessBuilder("docker","run","--rm"
                        ,"-i","--network","none","--memory","256m","-v",tempDirectory+":/sandbox"
                        ,"codeforge-judge:1.0","timeout",String.valueOf((version.getTimeLimit()+999)/1000),"java","Solution");
                runBuilder.redirectErrorStream(true);
                Process runProcess = runBuilder.start();

                runProcess.getOutputStream().write(testCase.getInput().getBytes());
                runProcess.getOutputStream().flush();
                runProcess.getOutputStream().close();

                boolean finished = runProcess.waitFor(version.getTimeLimit()+5000, TimeUnit.MILLISECONDS);
                if (!finished) {
                    runProcess.destroyForcibly();
                    result.setStatus("TIME_LIMIT_EXCEEDED");
                    return result;
                }
                int exited = runProcess.exitValue();
                // 检查运行时错误
                if ( exited== 124) {
                    result.setStatus("TIME_LIMIT_EXCEEDED");
                    result.setErrorMessage("Process exited with code " + exited);
                    return result;
                }

                if (exited != 0 ) {
                    result.setStatus( "RUNTIME_ERROR" );
                    result.setErrorMessage( "Process exited with code " + exited);
                    return result;
                }

                StringBuilder output = new StringBuilder();
                BufferedReader runReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                String outputLine;
                while ((outputLine = runReader.readLine()) != null) {
                    output.append(outputLine).append("\n");
                }
                String actual = output.toString();
                if (actual.trim().equals(testCase.getOutput().trim())) {
                    passedCases++;
                }
            }

            // 计算得分
            int score = (passedCases * 100) / totalCases;
            result.setStatus(passedCases == totalCases ? "ACCEPTED" : "WRONG_ANSWER");
            result.setScore(score);
            return result;
        } catch (IOException | InterruptedException e) {
            result.setStatus("RUNTIME_ERROR");
            result.setErrorMessage(e.getMessage());
            return result;
        } finally {
            // 7. 更新 submission
            submission.setStatus(result.getStatus());
            submission.setScore(result.getScore());
            submission.setErrorMessage(result.getErrorMessage());
            submission.setJudgeTime(LocalDateTime.now());
            submissionMapper.updateById(submission);

            // 清理临时目录
            if (tempDirectory != null) {
                try {
                    Files.walk(tempDirectory)
                            .sorted(Comparator.reverseOrder())
                            .forEach(p -> {
                                try {
                                    Files.delete(p);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}