package com.codeforge.codeforge.mq;

import com.codeforge.codeforge.judge.service.JudgeService;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * RabbitMQ 消费端幂等/可靠性测试：
 * - 判题成功或重复消息被跳过时都要 ACK，避免消息反复重投；
 * - 判题异常时带 x-retry-count 重新投递，超过 3 次进入死信队列。
 */
@ExtendWith(MockitoExtension.class)
class JudgeListenerTest {

    @Mock
    private JudgeService judgeService;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private Channel channel;
    @InjectMocks
    private JudgeListener judgeListener;

    @Test
    void acksAfterSuccessfulJudge() throws Exception {
        judgeListener.onJudgeMessage(42L, channel, 7L, 0);

        verify(judgeService).judge(42L);
        verify(channel).basicAck(7L, false);
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void acksWhenDuplicateMessageIsSkippedByService() throws Exception {
        // 终态重复消息：judge() 直接返回已有结果，监听器仍然正常 ACK
        judgeListener.onJudgeMessage(42L, channel, 8L, 0);

        verify(channel).basicAck(8L, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void republishesWithIncrementedRetryHeaderOnFailure() throws Exception {
        doThrow(new RuntimeException("judge crashed")).when(judgeService).judge(42L);
        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        judgeListener.onJudgeMessage(42L, channel, 9L, 1);

        verify(rabbitTemplate).convertAndSend(eq("judge.queue"), eq(42L), captor.capture());
        Message message = new Message(new byte[0]);
        captor.getValue().postProcessMessage(message);
        assertEquals(2, message.getMessageProperties().getHeaders().get("x-retry-count"));
        verify(channel).basicAck(9L, false);
    }

    @Test
    void deadLettersAfterThreeRetries() throws Exception {
        doThrow(new RuntimeException("judge crashed")).when(judgeService).judge(42L);

        judgeListener.onJudgeMessage(42L, channel, 10L, 3);

        verify(channel).basicNack(10L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
        verifyNoInteractions(rabbitTemplate);
    }
}
