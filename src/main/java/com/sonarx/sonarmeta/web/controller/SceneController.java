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
 * @description: Controller for scenes (combined with multiple models).
 * @author: liuxuanming
 */
@Api("场景信息接口")
@Slf4j
@RestController
@RequestMapping(API_PREFIX + "/scene")
public class SceneController {

    // TODO
    @ApiOperation(value = "创建场景作品")
    @RequestMapping(value = "/create", method = {RequestMethod.POST})
    public HttpResult createScene() {
        return  HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "编辑场景作品")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public HttpResult editScene() {
        return  HttpResult.successResult();
    }


}
