package com.codeforge.codeforge.mq;

import com.codeforge.codeforge.judge.service.JudgeService;
import com.codeforge.codeforge.submission.entity.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JudgeListener {

    private final JudgeService judgeService;

    @RabbitListener(queues = "judge.queue")
    public void onJudgeMessage (Long submissionId) {
        judgeService.judge(submissionId);
    }
}
