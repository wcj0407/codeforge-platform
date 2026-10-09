package com.codeforge.codeforge.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {
    @RabbitListener(queues = "hello.queue")
    public void receive(final String message) {
        System.out.println(message);
    }
}
