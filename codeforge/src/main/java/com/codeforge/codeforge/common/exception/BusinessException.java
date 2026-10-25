package com.codeforge.codeforge.common.exception;
import com.codeforge.codeforge.common.ErrorCode;
import lombok.Getter;

@Getter
//业务类型上的报错
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    public BusinessException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
