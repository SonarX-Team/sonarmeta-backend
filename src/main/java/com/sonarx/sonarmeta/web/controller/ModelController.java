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
 * @Description: Controller for models.
 * @author: liuxuanming
 */
@Api("模型信息接口")
@Slf4j
@RestController
@RequestMapping(API_PREFIX + "/model")
public class ModelController {

    @ApiOperation(value = "创建模型作品")
    @RequestMapping(value = "/create", method = {RequestMethod.POST})
    public HttpResult createModel() {
        return  HttpResult.successResult();
    }

    @ApiOperation(value = "编辑模型作品")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public HttpResult editModel() {
        return  HttpResult.successResult();
    }
}
