package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.form.CreateModelForm;
import com.sonarx.sonarmeta.domain.form.EditModelForm;
import com.sonarx.sonarmeta.domain.model.*;
import com.sonarx.sonarmeta.domain.view.ModelView;
import com.sonarx.sonarmeta.service.ModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Map;

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
    public HttpResult<ModelDO> createModel(@RequestBody @Validated CreateModelForm form) {
        ModelDO modelDO;
        try {
            modelDO = modelService.createModelWithForm(form);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(modelDO);
    }

    @ApiOperation(value = "编辑模型作品")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public HttpResult<ModelDO> editModel(@RequestBody @Validated EditModelForm form) {
        ModelDO modelDO;
        try {
            modelDO = modelService.editModelWithForm(form);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(modelDO);

    }

    @ApiOperation(value = "获取模型视图")
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    public HttpResult<ModelView> getModel(@RequestParam(value = "modelId") Long modelId) {
        ModelView modelView;
        try {
            modelView = modelService.getModelViewById(modelId);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(modelView);
    }

    @ApiOperation(value = "获取模型详细设置")
    @RequestMapping(value = "/detail", method = {RequestMethod.GET})
    public HttpResult<Map<String, Object>> getModelDetailSettings(@RequestParam(value = "modelId") Long modelId) {
        Map<String, Object> res;
        try {
            res = modelService.getModelDetailSettings(modelId);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(res);
    }


    @ApiOperation(value = "编辑模型基本设置")
    @RequestMapping(value = "/basic/edit", method = {RequestMethod.POST})
    public HttpResult<ModelBasicSettingsDO> editModelBasicSettings(@RequestBody ModelBasicSettingsDO modelBasicSettingsDO) {
        ModelBasicSettingsDO res;
        try {
            res = modelService.editModelBasicSettings(modelBasicSettingsDO);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(res);
    }


    @ApiOperation(value = "编辑模型光线设置")
    @RequestMapping(value = "/light/edit", method = {RequestMethod.POST})
    public HttpResult<ModelLightSettingsDO> editModelLightSettings(@RequestBody ModelLightSettingsDO modelLightSettingsDO) {
        ModelLightSettingsDO res;
        try {
            res = modelService.editModelLightSettings(modelLightSettingsDO);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(res);
    }


    @ApiOperation(value = "编辑模型材料设置")
    @RequestMapping(value = "/material/edit", method = {RequestMethod.POST})
    public HttpResult<ModelMaterialSettingsDO> editModelMaterialSettings(@RequestBody ModelMaterialSettingsDO modelMaterialSettingsDO) {
        ModelMaterialSettingsDO res;
        try {
            res = modelService.editModelMaterialSettings(modelMaterialSettingsDO);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(res);
    }



    @ApiOperation(value = "编辑模型后期设置")
    @RequestMapping(value = "/postprocessing/edit", method = {RequestMethod.POST})
    public HttpResult<ModelPostprocessingSettingsDO> editModelPostprocessingSettings(@RequestBody ModelPostprocessingSettingsDO modelPostprocessingSettingsDO) {
        ModelPostprocessingSettingsDO res = null;
        try {
            res = modelService.editModelPostprocessingSettings(modelPostprocessingSettingsDO);
        } catch (BusinessException e) {
            return HttpResult.errorResult(e.getMessage());
        }
        return HttpResult.successResult(res);

    }
}
