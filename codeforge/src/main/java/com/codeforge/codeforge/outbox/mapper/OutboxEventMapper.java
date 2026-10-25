package com.codeforge.codeforge.outbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codeforge.codeforge.outbox.entity.OutboxEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutboxEventMapper extends BaseMapper<OutboxEvent> {
}
