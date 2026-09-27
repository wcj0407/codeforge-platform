package com.codeforge.codeforge.submission.dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDetailVO {
    private Long id;
    private Long problemId;
    private Integer problemVersion;
    private Long userId;
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
