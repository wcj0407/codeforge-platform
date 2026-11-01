package com.codeforge.codeforge.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeforge.codeforge.outbox.entity.OutboxEvent;
import com.codeforge.codeforge.outbox.mapper.OutboxEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.StringUtils.truncate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayJob {
    private final OutboxEventMapper outboxEventMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    public void relay() {
        List<OutboxEvent> outboxEventList = outboxEventMapper.selectList(new LambdaQueryWrapper<OutboxEvent>()
                .eq(OutboxEvent::getStatus, "PENDING")
                .orderByAsc(OutboxEvent::getCreateTime)
                .last("limit 100"));

        for (OutboxEvent event : outboxEventList) {
            try {
                String queue = resolveQueue(event.getEventType());

                Long id = parseSubmissionId(event.getPayload());

                rabbitTemplate.convertAndSend(queue, id);

                event.setStatus("SENT");
                event.setSentTime(LocalDateTime.now());

                outboxEventMapper.updateById(event);
            } catch (Exception e) {
                // 单条消息解析/发送失败不能拖垮整批事件
                log.error("outbox 消息处理失败 id={}", event.getId(), e);
                event.setRetryCount(event.getRetryCount() == null ? 1 : event.getRetryCount() + 1);
                event.setErrorMessage(truncate(e.getMessage(), 500));
                outboxEventMapper.updateById(event);
            }
        }




    }

    private Long parseSubmissionId(String payload) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(payload);
        JsonNode idNode = node.get("submissionId");
        return idNode == null ? null : idNode.asLong();
    }

    private String resolveQueue(String eventType) {
        if ("JUDGE_SUBMISSION".equals(eventType)) {
            return "judge.queue";
        }
        throw new IllegalArgumentException("未知事件类型: " + eventType);
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return null;
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }

}
