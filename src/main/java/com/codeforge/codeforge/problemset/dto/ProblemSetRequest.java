package com.codeforge.codeforge.problemset.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProblemSetRequest {
    @NotBlank(message = "题单名称不能为空")
    private String title;

    private String description;

    /** 是否公开：不传默认为私有 */
    private Boolean isPublic;

    /** 题目 ID 列表（按顺序），可为空（先建空题单再加题） */
    private List<Long> problemIds;
}