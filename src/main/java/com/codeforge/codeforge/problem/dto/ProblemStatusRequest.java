package com.codeforge.codeforge.problem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProblemStatusRequest {
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "DRAFT|PUBLISHED", message = "状态只能为DRAFT/PUBLISHED")
    private String status;
}
