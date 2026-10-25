package com.codeforge.codeforge.submission.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionVO {
    private Long id;
    private Long problemId;
    /** 题目名称（题目被删除时为 null，前端用「题目 #id」兜底） */
    private String problemTitle;
    private Integer problemVersion;
    private Long userId;
    /** 提交人用户名（管理员查看全量提交时展示） */
    private String username;
    private String language;
    private String status;
    private Integer score;
    private Integer timeUsed;
    private Integer memoryUsed;
    private LocalDateTime submitTime;
    private LocalDateTime judgeTime;
}
