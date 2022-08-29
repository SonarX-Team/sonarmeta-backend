package com.sonarx.sonarmeta.common;

import com.sonarx.sonarmeta.domain.enums.BusinessError;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;

/**
 * @Description: 业务异常
 * @author: liuxuanming
 */
public class BusinessException extends Exception {

    //业务异常编码 @see ErrorCodeEnum
    private int code;

    //业务异常信息
    private String message;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
        this.code = ErrorCodeEnum.FAIL.getCode();
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(BusinessError businessError) {
        this.code = ErrorCodeEnum.FAIL.getCode();
        this.message = businessError.getDesc();
    }

    public static BusinessException fail(String message) {
        return new BusinessException(message);
    }

    public static BusinessException fail(ErrorCodeEnum errorCode) {
        return new BusinessException(errorCode.getCode(), errorCode.getDesc());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
