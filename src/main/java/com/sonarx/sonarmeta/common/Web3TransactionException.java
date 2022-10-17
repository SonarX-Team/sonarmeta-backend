package com.sonarx.sonarmeta.common;

import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;

/**
 * @description: Web3交易异常
 * @author: liuxuanming
 */
public class Web3TransactionException extends BusinessException {

    private int code;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    //业务异常信息
    private String message;

    public Web3TransactionException() {
        super();
    }

    public Web3TransactionException(String message) {
        super(message);
        this.code = ErrorCodeEnum.FAIL.getCode();
        this.message = "交易异常： " + message;
    }


}
