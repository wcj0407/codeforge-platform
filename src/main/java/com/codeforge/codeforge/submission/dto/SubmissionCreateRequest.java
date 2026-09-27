package com.codeforge.codeforge.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SubmissionCreateRequest {
    @NotNull(message = "题目id不能为空")
    private Long problemId;

    @NotBlank(message = "代码内容不能为空")
    private String code;

    @NotBlank(message = "代码语言不能为空")
    @Pattern(regexp="JAVA|PYTHON|CPP", message = "语言必须是 JAVA/PYTHON/CPP")
    private String language;
}
