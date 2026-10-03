package com.codeforge.codeforge;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * ProcessBuilder 学习实验
 */
public class ProcessBuilderTest {

    /**
     * 实验 1：执行简单的系统命令
     */
    @Test
    public void testSimpleCommand() throws Exception {
        // 创建 ProcessBuilder，传入要执行的命令
        ProcessBuilder pb = new ProcessBuilder("java", "-version");
        // 合并错误流到标准输出流（java -version 输出到 stderr）
        pb.redirectErrorStream(true);

        // 启动进程
        Process process = pb.start();

        // 读取输出
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );

        String line;
        System.out.println("=== 命令输出 ===");
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待进程结束，最多等待 5 秒
        boolean finished = process.waitFor(5, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly(); // 超时强制终止
            System.out.println("进程超时，已强制终止");
        }

        // 获取退出码（0 表示成功）
        int exitCode = process.exitValue();
        System.out.println("退出码: " + exitCode);
    }

    /**
     * 实验 2：编译并执行 Java 代码
     */
    @Test
    public void testCompileAndRun() throws Exception {
        // TODO: 你来实现这个实验
        // 1. 在临时目录创建 HelloWorld.java
        Path tempDir = Files.createTempDirectory("judge-");
        String code = """
                public class HelloWorld {
                public static void main(String[] args) {
                                    System.out.println("Hello, CodeForge!");
                                }
                }
                """;
        Path javaFile = tempDir.resolve("HelloWorld.java");
        Files.writeString(javaFile,code );
       ProcessBuilder pb = new ProcessBuilder("javac", "HelloWorld.java");
        pb.directory(tempDir.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        boolean finished = process.waitFor(5, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
        }
        int exitCode = process.exitValue();
        System.out.println(exitCode);


        ProcessBuilder pb1 = new ProcessBuilder("java", "HelloWorld");
        pb1.directory(tempDir.toFile());
        pb1.redirectErrorStream(true);
        Process process1 = pb1.start();

        BufferedReader br1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
        String line1;
        while ((line1 = br1.readLine()) != null) {
            System.out.println(line1);
        }
        boolean finished1 = process1.waitFor(5, TimeUnit.SECONDS);
        if (!finished1) {
            process1.destroyForcibly();
        }
        int exitCode1 = process1.exitValue();
        System.out.println(exitCode1);

        Files.walk(tempDir).sorted(Comparator.reverseOrder())
                .forEach(p->{
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
