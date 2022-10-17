package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.common.Constants;
import com.sonarx.sonarmeta.common.Web3TransactionException;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.common.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * RPC统一异常控制器
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.sonarx.sonarmeta.web.controller")
public class HttpExceptionController {

    /**
     * 捕捉参数校验异常
     * （BindException-用于捕捉@Valid校验产生的异常）
     *
     * @param e 参数校验异常类
     * @return 返回200正常码和HTTP200
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> handleMethodArgumentNotValidException(BindException e) {
        String bindErrMsg = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(Constants.DEFAULT_DELIMITER));
        log.error(bindErrMsg);
        return HttpResult.errorResult(ErrorCodeEnum.FAIL.getCode(), String.format("输入参数不合法-->%s", bindErrMsg));
    }

    /**
     * 捕捉参数校验异常
     * （MethodArgumentNotValidException-用于捕捉@Validated校验产生的异常）
     *
     * @param e 参数校验异常类
     * @return 返回200正常码和HTTP200
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String bindErrMsg = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(Constants.DEFAULT_DELIMITER));
        log.error(bindErrMsg);
        return HttpResult.errorResult(ErrorCodeEnum.FAIL.getCode(), String.format("输入参数不合法-->%s", bindErrMsg));
    }

    /**
     * 捕捉参数校验异常（用于捕捉@RequestParam校验产生的异常）
     *
     * @param e 参数校验异常类
     * @return 返回200正常码和HTTP200
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> handleMethodArgumentNotValidException(Exception e) {
        log.error(e.getMessage(), e);
        return HttpResult.errorResult(ErrorCodeEnum.FAIL.getCode(), String.format("输入参数不合法-->%s", e.getMessage()));
    }

    /**
     * 捕获业务异常
     *
     * @param e 业务异常类
     * @return HttpResult
     */
    @ExceptionHandler({BusinessException.class, Web3TransactionException.class})
    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return HttpResult.errorResult(e.getCode(), e.getMessage());
    }

    /**
     * 捕捉其他所有异常
     *
     * @param e 异常类
     * @return 返回200正常码和HTTP200
     */
    @SuppressWarnings("unused")
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> globalException(Exception e) {
        log.error("服务器内部错误", e);
        return HttpResult.errorResult("服务器内部错误");
    }
}
