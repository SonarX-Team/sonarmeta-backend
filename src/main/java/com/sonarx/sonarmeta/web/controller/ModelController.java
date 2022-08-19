package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.form.CreateModelForm;
import com.sonarx.sonarmeta.domain.form.EditModelForm;
import com.sonarx.sonarmeta.service.ModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @Resource
    ModelService modelService;

    @ApiOperation(value = "创建模型作品")
    @RequestMapping(value = "/create", method = {RequestMethod.POST})
    public HttpResult createModel(@RequestBody @Validated CreateModelForm form) {
        modelService.createModelWithForm(form);
        return HttpResult.successResult();
    }

    @ApiOperation(value = "编辑模型作品")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public HttpResult editModel(@RequestBody @Validated EditModelForm form) {
        modelService.editModelWithForm(form);
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "获取模型基本设置")
    @RequestMapping(value = "/basic", method = {RequestMethod.GET})
    public HttpResult getModelBasicSettings() {
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "编辑模型基本设置")
    @RequestMapping(value = "/basic/edit", method = {RequestMethod.POST})
    public HttpResult editModelBasicSettings() {
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "获取模型基本设置")
    @RequestMapping(value = "/light", method = {RequestMethod.GET})
    public HttpResult getModelLightSettings() {
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "编辑模型光线设置")
    @RequestMapping(value = "/light/edit", method = {RequestMethod.POST})
    public HttpResult editModelLightSettings() {
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "获取模型材料设置")
    @RequestMapping(value = "/material", method = {RequestMethod.GET})
    public HttpResult getModelMaterialSettings() {
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "编辑模型材料设置")
    @RequestMapping(value = "/material/edit", method = {RequestMethod.POST})
    public HttpResult editModelMaterialSettings() {
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "获取模型后期设置")
    @RequestMapping(value = "/postprocessing", method = {RequestMethod.GET})
    public HttpResult getModelPostProcessingSettings() {
        return HttpResult.successResult();
    }

    // TODO
    @ApiOperation(value = "编辑模型后期设置")
    @RequestMapping(value = "/postprocessing/edit", method = {RequestMethod.POST})
    public HttpResult editModelPostprocessingSettings() {
        return HttpResult.successResult();
    }
}
