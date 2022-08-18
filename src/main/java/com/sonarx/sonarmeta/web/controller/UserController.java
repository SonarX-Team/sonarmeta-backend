package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.domain.common.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.sonarx.sonarmeta.common.Constants.API_PREFIX;

/**
 * @Description: Default controller for test.
 * @author: liuxuanming
 */
@Api("用户信息接口")
@Slf4j
@RestController
@RequestMapping(API_PREFIX + "/user")
public class UseController {

    @ApiOperation(value = "获取用户个人空间作品列表")
    @RequestMapping(value = "/workslist", method = {RequestMethod.GET})
    public HttpResult getWorksList() {
            return  HttpResult.successResult();
    }

}
