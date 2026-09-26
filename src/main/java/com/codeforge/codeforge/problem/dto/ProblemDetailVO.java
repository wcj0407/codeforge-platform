package com.codeforge.codeforge.problem.dto;

import com.codeforge.codeforge.problem.entity.ProblemVersion;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProblemDetailVO {
    private Long id;
    private String title;
    private String difficulty;
    private List<String> tags;
    private String status;
    private Long creatorId;
    private Integer version;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<ProblemVersion.TestCase> testCases;
    //版本创建时间
    private LocalDateTime createdAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
