package com.codeforge.codeforge.mq;

import com.codeforge.codeforge.judge.entity.JudgeFailRecord;
import com.codeforge.codeforge.judge.mapper.JudgeFailRecordMapper;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterListener {

    private final SubmissionMapper submissionMapper;
    private final JudgeFailRecordMapper judgeFailRecordMapper;

    @RabbitListener(queues = "dead.queue")
    public void onDeadMessage(Long submissionId,
                              @Header(value = "x-retry-count", required = false) Integer retryCount,
                              Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            Submission submission = submissionMapper.selectById(submissionId);
            JudgeFailRecord record = new JudgeFailRecord();
            record.setSubmissionId(submissionId);
            if (submission != null) {
                record.setProblemId(submission.getProblemId());
                record.setUserId(submission.getUserId());
                record.setFailReason(submission.getErrorMessage());
            }
            record.setRetryCount(retryCount == null ? 0 : retryCount);
            record.setCreatedAt(LocalDateTime.now());
            judgeFailRecordMapper.insert(record);
        } catch (Exception e) {
            log.error("死信持久化失败 submissionId={}", submissionId, e);
        } finally {
            channel.basicAck(deliveryTag, false);
        }
    }
}