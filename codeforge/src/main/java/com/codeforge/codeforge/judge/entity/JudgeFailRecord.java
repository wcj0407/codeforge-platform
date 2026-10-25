package com.codeforge.codeforge.judge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "judge_fail_record")
public class JudgeFailRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long submissionId;
    private Long problemId;
    private Long userId;
    private String failReason;
    private Integer retryCount;
    private LocalDateTime createdAt;
}