package com.codeforge.codeforge.problem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "problem_version", autoResultMap = true)
public class ProblemVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long problemId;

    private Integer version;

    private String description;

    private String inputFormat;

    private String outputFormat;

    private Integer timeLimit;

    private Integer memoryLimit;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<TestCase> testCases;

    private Long createdBy;

    private LocalDateTime createdAt;

    @Data
    public static class TestCase {
        private String input;
        private String output;
    }
}