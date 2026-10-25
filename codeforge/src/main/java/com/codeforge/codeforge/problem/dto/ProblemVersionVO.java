package com.codeforge.codeforge.problem.dto;

import com.codeforge.codeforge.problem.entity.ProblemVersion;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class ProblemVersionVO {
    private Long id;
    private Long problemId;
    private Integer version;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<ProblemVersion.TestCase> testCases;
    private Long createdBy;
    private LocalDateTime createdAt;
}
