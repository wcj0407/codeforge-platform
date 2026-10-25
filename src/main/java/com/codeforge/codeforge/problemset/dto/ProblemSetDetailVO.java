package com.codeforge.codeforge.problemset.dto;

import com.codeforge.codeforge.problem.dto.ProblemVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProblemSetDetailVO {
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    private Boolean isPublic;
    private LocalDateTime createTime;
    /** 题单内的题目列表（按 sort_no 排序） */
    private List<ProblemVO> problems;
}