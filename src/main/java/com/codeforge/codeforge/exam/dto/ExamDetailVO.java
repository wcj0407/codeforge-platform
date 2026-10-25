package com.codeforge.codeforge.exam.dto;

import com.codeforge.codeforge.problem.dto.ProblemVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDetailVO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long creatorId;
    private LocalDateTime createTime;
    private List<ProblemVO> problems;
    /** 当前用户在这场考核里已通过的题目 id 列表 */
    private List<Long> solvedProblemIds;
}