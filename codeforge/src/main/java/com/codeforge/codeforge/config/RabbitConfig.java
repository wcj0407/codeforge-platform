package com.codeforge.codeforge.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue judgeQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "judge-dlx");
        arguments.put("x-dead-letter-routing-key", "dead");
        return new Queue("judge.queue",true, false, false, arguments);


    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange("judge-dlx", true, false);
    }

    @Bean
    public Queue deadQueue() {
        return new Queue("dead.queue", true);
    }

    @Bean
    public Binding deadBinding() {
        return  BindingBuilder.bind(deadQueue()).to(deadExchange()).with("dead");
    }
}
