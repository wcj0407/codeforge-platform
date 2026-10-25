package com.codeforge.codeforge.problem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "problem", autoResultMap = true)
public class Problem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String difficulty;
    @TableField(typeHandler= JacksonTypeHandler.class)
    private List<String> tags;
    private String status;
    /** 审核驳回原因（仅 REJECTED 状态时有值） */
    private String reviewComment;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
