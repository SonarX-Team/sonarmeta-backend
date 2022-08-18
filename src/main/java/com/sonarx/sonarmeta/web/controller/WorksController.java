package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.domain.common.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.sonarx.sonarmeta.common.Constants.API_PREFIX;

/**
 * @Description: Controller for works.
 * @author: liuxuanming
 */
@Api("作品信息接口")
@Slf4j
@RestController
@RequestMapping(API_PREFIX + "/works")
public class WorksController {

    @ApiOperation(value = "作品列表")
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    public HttpResult getWorksList() {
        return  HttpResult.successResult();
    }

    @ApiOperation(value = "搜索作品")
    @RequestMapping(value = "/search", method = {RequestMethod.GET})
    public HttpResult searchWorks() {
        return  HttpResult.successResult();
    }
}
