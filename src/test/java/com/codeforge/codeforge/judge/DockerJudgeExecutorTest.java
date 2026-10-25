package com.codeforge.codeforge.judge;

import com.codeforge.codeforge.judge.dto.JudgeResult;
import com.codeforge.codeforge.judge.service.DockerJudgeExecutor;
import com.codeforge.codeforge.problem.entity.ProblemTestCase;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 判题核心链路测试：AC / WA / CE / RE / TLE，以及 Docker 内存限制。
 * 依赖本机 Docker 和 codeforge-judge:1.0 镜像；环境不满足时自动跳过，不污染 CI。
 */
@Timeout(value = 180, unit = TimeUnit.SECONDS)
class DockerJudgeExecutorTest {

    private static final String SUM_CODE = """
            import java.util.*;
            public class Solution {
                public static void main(String[] args) {
                    Scanner sc = new Scanner(System.in);
                    int a = sc.nextInt();
                    int b = sc.nextInt();
                    System.out.println(a + b);
                }
            }
            """;

    private final DockerJudgeExecutor executor = new DockerJudgeExecutor();

    @BeforeAll
    static void requireDockerImage() {
        Assumptions.assumeTrue(imageAvailable(), "跳过：本机 Docker 或 codeforge-judge:1.0 镜像不可用");
    }

    @Test
    void acceptedWhenOutputMatches() {
        JudgeResult result = executor.execute(SUM_CODE, List.of(testCase("1 2", "3")), 1000, 256);

        assertEquals("ACCEPTED", result.getStatus());
        assertEquals(100, result.getScore());
    }

    @Test
    void wrongAnswerWhenOutputDiffers() {
        String code = """
                public class Solution {
                    public static void main(String[] args) {
                        System.out.println(0);
                    }
                }
                """;

        JudgeResult result = executor.execute(code, List.of(testCase("1 2", "3")), 1000, 256);

        assertEquals("WRONG_ANSWER", result.getStatus());
        assertEquals(0, result.getScore());
    }

    @Test
    void compileErrorForInvalidJava() {
        JudgeResult result = executor.execute("public class Solution {", List.of(testCase("1", "1")), 1000, 256);

        assertEquals("COMPILE_ERROR", result.getStatus());
    }

    @Test
    void runtimeErrorForUncaughtException() {
        String code = """
                public class Solution {
                    public static void main(String[] args) {
                        throw new RuntimeException("boom");
                    }
                }
                """;

        JudgeResult result = executor.execute(code, List.of(testCase("", "")), 1000, 256);

        assertEquals("RUNTIME_ERROR", result.getStatus());
    }

    @Test
    void timeLimitExceededForInfiniteLoop() {
        String code = """
                public class Solution {
                    public static void main(String[] args) {
                        while (true) { }
                    }
                }
                """;

        JudgeResult result = executor.execute(code, List.of(testCase("", "")), 1000, 256);

        assertEquals("TIME_LIMIT_EXCEEDED", result.getStatus());
    }

    @Test
    void memoryLimitKillsOversizedAllocation() {
        String code = """
                public class Solution {
                    public static void main(String[] args) {
                        byte[] data = new byte[512 * 1024 * 1024];
                        System.out.println(data.length);
                    }
                }
                """;

        JudgeResult result = executor.execute(code, List.of(testCase("", "536870912")), 2000, 128);

        assertNotEquals("ACCEPTED", result.getStatus());
        assertEquals("RUNTIME_ERROR", result.getStatus());
    }

    private static ProblemTestCase testCase(String input, String output) {
        ProblemTestCase testCase = new ProblemTestCase();
        testCase.setInput(input);
        testCase.setOutput(output);
        testCase.setIsPublic(1);
        return testCase;
    }

    private static boolean imageAvailable() {
        try {
            Process process = new ProcessBuilder("docker", "image", "inspect", "codeforge-judge:1.0")
                    .redirectErrorStream(true)
                    .start();
            return process.waitFor(10, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
