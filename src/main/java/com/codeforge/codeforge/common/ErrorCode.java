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
    UNAUTHORIZED(40100,"未登录"),
    FORBIDDEN(40300,"无权限"),
    PROBLEM_NOT_FOUND( 40401 , "题目不存在" ),
    SUBMISSION_NOT_FOUND( 40402 , "提交记录不存在" ),
    SYSTEM_ERROR(50000,"系统错误");

    private final int code;
    private final String message;
    private ErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
