package com.codeforge.codeforge.problem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
//返回前端简单的信息
@Data
public class ProblemVO {
    private Long id;
    private String title;
    private String difficulty;
    private List<String> tags;
    private String status;
    /** 审核驳回原因（仅 REJECTED 状态时返回） */
    private String reviewComment;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
