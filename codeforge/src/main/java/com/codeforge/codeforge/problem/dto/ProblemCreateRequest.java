package com.codeforge.codeforge.problem.dto;

import com.codeforge.codeforge.problem.entity.ProblemVersion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class ProblemCreateRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "难度不能为空")
    @Pattern(regexp = "EASY|MEDIUM|HARD",message = "难度只能为EASY/MEDIUM/HARD")
    private String difficulty;

    @NotEmpty(message = "标签不能为空")
    private List<String> tags;

    @NotBlank(message = "描述不能为空")
    private String description;

    private String inputFormat;
    private String outputFormat;

    @NotNull(message = "时间不能为空")
    private Integer timeLimit;
    @NotNull(message = "内存不能为空")
    private Integer memoryLimit;

    @NotEmpty(message = "测试用例不能为空")
    private List<ProblemVersion.TestCase> testCases;
}
