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
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
