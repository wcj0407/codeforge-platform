package com.codeforge.codeforge;

import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JudgeAsyncTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Test
    public void testAsyncJudge() throws Exception {
        // 1. 把提交1重置为 PENDING，方便看清状态变化
        Submission sub = submissionMapper.selectById(1L);
        sub.setStatus("PENDING");
        submissionMapper.updateById(sub);
        System.out.println("提交1 已重置为 PENDING");

        // 2. 发消息：把提交1的 id 丢进判题队列（这就是异步的"提交"）
        rabbitTemplate.convertAndSend("judge.queue", 1L);

        // 3. 等 8 秒，让监听器消费消息并判题
        Thread.sleep(8000);

        // 4. 查最终状态
        Submission after = submissionMapper.selectById(1L);
        System.out.println("最终状态: " + after.getStatus());
    }
}