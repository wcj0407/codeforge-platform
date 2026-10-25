package com.codeforge.codeforge.judge.dto;

import lombok.Data;

@Data
public class JudgeResult {
    private String status;
    private Integer score;
    private String errorMessage;
    private Integer timeUsed;
    private Integer memoryUsed;
}