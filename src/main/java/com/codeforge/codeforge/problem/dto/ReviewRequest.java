package com.codeforge.codeforge.problem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员审核题目请求。
 * approved=true 表示通过，false 表示驳回。
 */
@Data
public class ReviewRequest {

    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    /** 驳回原因：驳回时必填，通过时可为空 */
    private String comment;
}