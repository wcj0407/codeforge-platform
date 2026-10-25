package com.codeforge.codeforge.common;

import lombok.Getter;

@Getter
//枚举状态码
public enum ErrorCode {
    SUCCESS(200,"操作成功"),
    PARAM_ERROR(40000,"参数错误"),
    USERNAME_EXISTS(40001,"用户名已经存在"),
    USER_NOT_FOUND(40002,"用户没有找到"),
    PASSWORD_ERROR(40003,"密码错误"),
    ACCOUNT_DISABLED( 40004 , "账号已被禁用" ),
    EMAIL_EXISTS(40005, "邮箱已被注册"),
    RESET_CODE_INVALID(40006, "验证码无效或已过期"),
    UNAUTHORIZED(40100,"未登录"),
    FORBIDDEN(40300,"无权限"),
    PROBLEM_STATUS_ERROR(40020,"题目状态不允许当前操作"),
    PROBLEM_NOT_PUBLISHED(40021,"题目未发布，无法提交"),
    PROBLEM_NOT_FOUND( 40401 , "题目不存在" ),
    SUBMISSION_NOT_FOUND( 40402 , "提交记录不存在" ),
    PROBLEM_SET_NOT_FOUND(40403, "题单不存在"),
    EXAM_NOT_FOUND(40404, "考核不存在"),
    SYSTEM_ERROR(50000,"系统错误");

    private final int code;
    private final String message;
    private ErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
