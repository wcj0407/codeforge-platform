package com.codeforge.codeforge.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "\\d{6}", message = "验证码为6位数字")
    private String code;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "新密码至少6位")
    private String newPassword;
}