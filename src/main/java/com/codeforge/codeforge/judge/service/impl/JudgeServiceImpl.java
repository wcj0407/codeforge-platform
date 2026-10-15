package com.codeforge.codeforge.judge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeforge.codeforge.judge.dto.JudgeResult;
import com.codeforge.codeforge.judge.service.JudgeService;
import com.codeforge.codeforge.problem.entity.ProblemTestCase;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemTestCaseMapper;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {
    /** 沙箱输出中，用来回传容器峰值内存的标记前缀（随程序输出一起返回，读取时会被剔除） */
    private static final String MEMORY_MARKER = "__MEM_PEAK__=";

    private final SubmissionMapper submissionMapper;
    private final ProblemVersionMapper problemVersionMapper;
    private final ProblemTestCaseMapper problemTestCaseMapper;

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
        int totalCases = 0;
        long maxTimeMs = 0;
        long maxMemoryBytes = 0;
        boolean hasRun = false;

        try {
            // 版本缺失时无法判题，直接给出系统错误
            if (version == null) {
                result.setStatus("SYSTEM_ERROR");
                result.setErrorMessage("题目版本不存在，无法判题");
                return result;
            }
            // 从独立表读该版本的全部用例（公开 + 隐藏都参与判题）
            List<ProblemTestCase> testCases = problemTestCaseMapper.selectList(
                    new LambdaQueryWrapper<ProblemTestCase>()
                            .eq(ProblemTestCase::getProblemVersionId, version.getId())
                            .orderByAsc(ProblemTestCase::getSortNo));
            if (testCases.isEmpty()) {
                result.setStatus("SYSTEM_ERROR");
                result.setErrorMessage("该题目没有测试用例");
                return result;
            }
            totalCases = testCases.size();

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
            String timeLimitSeconds = String.valueOf((version.getTimeLimit()+999)/1000);
            // 运行程序后，顺带回传容器 cgroup 的峰值内存（cgroup v2 与 v1 路径都尝试）
            String runScript = "timeout " + timeLimitSeconds + " java Solution; ec=$?; "
                    + "p=$(cat /sys/fs/cgroup/memory.peak 2>/dev/null"
                    + " || cat /sys/fs/cgroup/memory/memory.max_usage_in_bytes 2>/dev/null"
                    + " || echo 0); echo \"" + MEMORY_MARKER + "$p\"; exit $ec";
            for (ProblemTestCase testCase : testCases) {
                ProcessBuilder runBuilder = new ProcessBuilder("docker","run","--rm"
                        ,"-i","--network","none","--memory",version.getMemoryLimit()+"m","-v",tempDirectory+":/sandbox"
                        ,"codeforge-judge:1.0","sh","-c",runScript);
                runBuilder.redirectErrorStream(true);
                Process runProcess = runBuilder.start();
                hasRun = true;

                runProcess.getOutputStream().write(testCase.getInput().getBytes());
                runProcess.getOutputStream().flush();
                runProcess.getOutputStream().close();

                long startNano = System.nanoTime();
                boolean finished = runProcess.waitFor(version.getTimeLimit()+5000, TimeUnit.MILLISECONDS);
                long elapsedMs = (System.nanoTime() - startNano) / 1_000_000;
                if (elapsedMs > maxTimeMs) {
                    maxTimeMs = elapsedMs;
                }

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
                // 剔除内存标记行，其余才是程序真实输出
                long memoryBytes = 0;
                StringBuilder cleanedOutput = new StringBuilder();
                for (String rawLine : output.toString().split("\n")) {
                    if (rawLine.startsWith(MEMORY_MARKER)) {
                        try {
                            memoryBytes = Long.parseLong(rawLine.substring(MEMORY_MARKER.length()).trim());
                        } catch (NumberFormatException ignored) {
                            // cgroup 不可读时保持 0
                        }
                    } else {
                        cleanedOutput.append(rawLine).append("\n");
                    }
                }
                if (memoryBytes > maxMemoryBytes) {
                    maxMemoryBytes = memoryBytes;
                }
                String actual = cleanedOutput.toString();
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
            result.setStatus("SYSTEM_ERROR");
            result.setErrorMessage(e.getMessage());
            throw new RuntimeException("判题系统故障：" + e.getMessage(), e);
        } finally {
            // 回填耗时/内存（只有真正跑过测试用例才有值）
            if (hasRun) {
                result.setTimeUsed((int) maxTimeMs);
                if (maxMemoryBytes > 0) {
                    result.setMemoryUsed((int) (maxMemoryBytes / 1024));
                }
            }
            // 7. 更新 submission
            submission.setStatus(result.getStatus());
            submission.setScore(result.getScore());
            submission.setErrorMessage(result.getErrorMessage());
            submission.setTimeUsed(result.getTimeUsed());
            submission.setMemoryUsed(result.getMemoryUsed());
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
