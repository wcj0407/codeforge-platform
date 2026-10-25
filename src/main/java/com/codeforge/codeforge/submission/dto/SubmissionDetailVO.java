package com.codeforge.codeforge.submission.dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDetailVO {
    private Long id;
    private Long problemId;
    /** 题目名称（题目被删除时为 null） */
    private String problemTitle;
    private Integer problemVersion;
    private Long userId;
    /** 提交人用户名 */
    private String username;
    private String code;
    private String language;
    private String status;
    private Integer score;
    private Integer timeUsed;
    private Integer memoryUsed;
    private String errorMessage;
    private LocalDateTime submitTime;
    private LocalDateTime judgeTime;
}
