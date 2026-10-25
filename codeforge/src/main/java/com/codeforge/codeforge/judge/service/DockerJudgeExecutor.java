package com.codeforge.codeforge.judge.service;

import com.codeforge.codeforge.judge.dto.JudgeResult;
import com.codeforge.codeforge.problem.entity.ProblemTestCase;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Docker 沙箱判题执行器：只负责「编译 + 逐用例运行 + 结果比对」，不碰数据库。
 * 抽出来后可以独立测试 AC / WA / CE / RE / TLE 以及容器内存限制。
 */
@Component
public class DockerJudgeExecutor {

    /** 沙箱输出中，用来回传容器峰值内存的标记前缀（随程序输出一起返回，读取时会被剔除） */
    private static final String MEMORY_MARKER = "__MEM_PEAK__=";
    private static final String IMAGE = "codeforge-judge:1.0";
    private static final int COMPILE_TIMEOUT_SECONDS = 10;
    private static final int RUN_EXTRA_WAIT_MS = 5000;

    public JudgeResult execute(String code, List<ProblemTestCase> testCases,
                               int timeLimitMs, int memoryLimitMb) {
        JudgeResult result = new JudgeResult();
        long maxTimeMs = 0;
        long maxMemoryBytes = 0;
        boolean hasRun = false;
        Path workspace = null;

        try {
            workspace = Files.createTempDirectory("judge-");
            Files.writeString(workspace.resolve("Solution.java"), code);

            CompileOutcome compile = compile(workspace);
            if (!compile.success()) {
                result.setStatus("COMPILE_ERROR");
                result.setErrorMessage(compile.output());
                return result;
            }

            String runScript = buildRunScript(timeLimitMs);
            int passedCases = 0;

            for (ProblemTestCase testCase : testCases) {
                RunOutcome run = runOnce(workspace, testCase.getInput(), timeLimitMs, memoryLimitMb, runScript);
                hasRun = true;
                maxTimeMs = Math.max(maxTimeMs, run.elapsedMs());

                if (run.timedOut()) {
                    result.setStatus("TIME_LIMIT_EXCEEDED");
                    result.setErrorMessage("运行超时，已被强制终止");
                    return result;
                }
                if (run.exitCode() == 124) {
                    result.setStatus("TIME_LIMIT_EXCEEDED");
                    result.setErrorMessage("Process exited with code " + run.exitCode());
                    return result;
                }
                if (run.exitCode() != 0) {
                    result.setStatus("RUNTIME_ERROR");
                    result.setErrorMessage("Process exited with code " + run.exitCode());
                    return result;
                }

                maxMemoryBytes = Math.max(maxMemoryBytes, run.memoryBytes());
                if (run.output().trim().equals(testCase.getOutput().trim())) {
                    passedCases++;
                }
            }

            int score = (passedCases * 100) / testCases.size();
            result.setStatus(passedCases == testCases.size() ? "ACCEPTED" : "WRONG_ANSWER");
            result.setScore(score);
            return result;
        } catch (IOException e) {
            result.setStatus("SYSTEM_ERROR");
            result.setErrorMessage(e.getMessage());
            throw new RuntimeException("判题系统故障：" + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.setStatus("SYSTEM_ERROR");
            result.setErrorMessage(e.getMessage());
            throw new RuntimeException("判题系统故障：" + e.getMessage(), e);
        } finally {
            if (hasRun) {
                result.setTimeUsed((int) maxTimeMs);
                if (maxMemoryBytes > 0) {
                    result.setMemoryUsed((int) (maxMemoryBytes / 1024));
                }
            }
            cleanup(workspace);
        }
    }

    private CompileOutcome compile(Path workspace) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("docker", "run", "--rm",
                "--network", "none", "--memory", "512m",
                "-v", workspace + ":/sandbox",
                IMAGE, "javac", "Solution.java");
        builder.redirectErrorStream(true);
        Process process = builder.start();

        StringBuilder output = new StringBuilder();
        Thread reader = drainAsync(process, output);

        boolean finished = process.waitFor(COMPILE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            reader.join(1000);
            return new CompileOutcome(false, "编译超时（超过 " + COMPILE_TIMEOUT_SECONDS + " 秒）");
        }
        reader.join(1000);
        return new CompileOutcome(process.exitValue() == 0, output.toString());
    }

    private RunOutcome runOnce(Path workspace, String input, int timeLimitMs, int memoryLimitMb,
                               String runScript) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("docker", "run", "--rm",
                "-i", "--network", "none", "--memory", memoryLimitMb + "m",
                "-v", workspace + ":/sandbox",
                IMAGE, "sh", "-c", runScript);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().flush();
        process.getOutputStream().close();

        StringBuilder rawOutput = new StringBuilder();
        Thread reader = drainAsync(process, rawOutput);

        long startNano = System.nanoTime();
        boolean finished = process.waitFor(timeLimitMs + RUN_EXTRA_WAIT_MS, TimeUnit.MILLISECONDS);
        long elapsedMs = (System.nanoTime() - startNano) / 1_000_000;

        if (!finished) {
            process.destroyForcibly();
            reader.join(1000);
            return new RunOutcome("", -1, true, elapsedMs, 0);
        }
        reader.join(1000);

        long memoryBytes = 0;
        StringBuilder cleanedOutput = new StringBuilder();
        for (String rawLine : rawOutput.toString().split("\n")) {
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
        return new RunOutcome(cleanedOutput.toString(), process.exitValue(), false, elapsedMs, memoryBytes);
    }

    /** 异步读取进程输出，避免输出把管道写满导致进程卡住 */
    private Thread drainAsync(Process process, StringBuilder sink) {
        Thread thread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sink.append(line).append("\n");
                }
            } catch (IOException ignored) {
                // 进程被强杀时读取会中断，忽略即可
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private String buildRunScript(int timeLimitMs) {
        String timeLimitSeconds = String.valueOf((timeLimitMs + 999) / 1000);
        // 运行程序后，顺带回传容器 cgroup 的峰值内存（cgroup v2 与 v1 路径都尝试）
        return "timeout " + timeLimitSeconds + " java Solution; ec=$?; "
                + "p=$(cat /sys/fs/cgroup/memory.peak 2>/dev/null"
                + " || cat /sys/fs/cgroup/memory/memory.max_usage_in_bytes 2>/dev/null"
                + " || echo 0); echo \"" + MEMORY_MARKER + "$p\"; exit $ec";
    }

    private void cleanup(Path workspace) {
        if (workspace == null) {
            return;
        }
        try {
            Files.walk(workspace)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private record CompileOutcome(boolean success, String output) {
    }

    private record RunOutcome(String output, int exitCode, boolean timedOut, long elapsedMs, long memoryBytes) {
    }
}
