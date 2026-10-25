package com.codeforge.codeforge.problemset.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProblemSetVO {
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    /** 是否公开：true 公开 / false 私有 */
    private Boolean isPublic;
    /** 题单内题目数量 */
    private Long problemCount;
    private LocalDateTime createTime;
}