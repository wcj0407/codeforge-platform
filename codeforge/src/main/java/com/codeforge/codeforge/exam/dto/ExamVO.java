package com.codeforge.codeforge.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamVO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /** 状态：NOT_STARTED / RUNNING / ENDED（由时间实时推导） */
    private String status;
    private Long problemCount;
    private LocalDateTime createTime;
}