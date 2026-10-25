package com.codeforge.codeforge.mq;

import com.codeforge.codeforge.judge.service.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JudgeListener {

    private final JudgeService judgeService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "judge.queue")
    public void onJudgeMessage (Long submissionId, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                @Header(value = "x-retry-count", required = false) Integer deliveryCount) throws IOException {

        if (deliveryCount == null ) {
            deliveryCount = 0 ;
        }

        try {
            judgeService.judge(submissionId);
            channel.basicAck(deliveryTag, false );
        } catch (Exception e) {
            Map<String,Object> headers = new HashMap<>();
            if (deliveryCount < 3 ) {

                headers.put( "x-retry-count" , deliveryCount + 1 );
                rabbitTemplate.convertAndSend( "judge.queue" , submissionId, msg -> {
                    msg.getMessageProperties().getHeaders().putAll(headers);
                    return msg;
                });
                channel.basicAck(deliveryTag, false );
            } else {
                channel.basicNack(deliveryTag, false , false );
            }
        }

    }
}
