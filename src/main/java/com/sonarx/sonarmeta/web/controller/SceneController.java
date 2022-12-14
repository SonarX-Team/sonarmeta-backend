package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.form.CreateSceneForm;
import com.sonarx.sonarmeta.domain.form.EditSceneForm;
import com.sonarx.sonarmeta.domain.model.SceneDO;
import com.sonarx.sonarmeta.domain.view.SceneView;
import com.sonarx.sonarmeta.service.SceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @Resource
    SceneService sceneService;

    @ApiOperation(value = "创建场景作品")
    @RequestMapping(value = "/create", method = {RequestMethod.POST})
    public HttpResult<SceneDO> createScene(@RequestBody @Validated CreateSceneForm createSceneForm) {
        SceneDO sceneDO;
        try {
            sceneDO = sceneService.createSceneWithForm(createSceneForm);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(sceneDO);
    }


    @ApiOperation(value = "编辑场景作品")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public HttpResult<SceneDO> editScene(@RequestBody @Validated EditSceneForm editSceneForm) {
        SceneDO sceneDO;
        try {
            sceneDO = sceneService.editSceneWithForm(editSceneForm);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return  HttpResult.successResult(sceneDO);
    }

    @ApiOperation(value = "获取场景作品")
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    public HttpResult<SceneView> getScene(@RequestParam(value = "sceneId") Long sceneId) {
        SceneView scene;
        try {
            scene = sceneService.getScene(sceneId);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return  HttpResult.successResult(scene);
    }

}
