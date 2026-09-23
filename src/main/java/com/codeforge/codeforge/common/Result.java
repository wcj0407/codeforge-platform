package com.codeforge.codeforge.common;

import lombok.Getter;

import java.util.UUID;

@Getter
//统一返回
public class Result <T>{
    private int code;
    private String message;
    private T data;
    private String traceId;
    private Result(int code, String message, T data, String traceId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
    }
    public static<T> Result<T> success(T data) {
        return new Result<>(
                ErrorCode.SUCCESS.getCode(),
                ErrorCode.SUCCESS.getMessage(),
                data,
                currentTraceId()
        );
    }
    public static<T> Result<T> success() {
        return success(null);
    }
    public static<T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                currentTraceId()
        );
    }
    public static<T> Result<T> error(String message) {
        return new Result<>(
                ErrorCode.SYSTEM_ERROR.getCode(),
                message,
                null,
                currentTraceId()
        );
    }

    public static <T> Result <T> error ( int code, String message) {
        return new Result <>(code, message, null , currentTraceId());
    }


    private static String currentTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
