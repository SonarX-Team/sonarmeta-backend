package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.CreateModelForm;
import com.sonarx.sonarmeta.domain.form.EditModelForm;
import com.sonarx.sonarmeta.domain.model.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sonarx.sonarmeta.domain.view.ModelView;

import java.util.Map;

/**
* @author hinsliu
* @description 针对表【t_model(模型信息)】的数据库操作Service
* @createDate 2022-08-18 21:40:29
*/
public interface ModelService extends IService<ModelDO> {

    ModelDO createModelWithForm(CreateModelForm form) throws BusinessException;

    ModelDO editModelWithForm(EditModelForm form) throws BusinessException;

    ModelDO getModelById(Long id) throws BusinessException;

    ModelView getModelViewById(Long id) throws BusinessException;

    ModelBasicSettingsDO editModelBasicSettings(ModelBasicSettingsDO modelBasicSettingsDO) throws BusinessException;

    Map<String, Object> getModelDetailSettings(Long modelId) throws BusinessException;

    ModelLightSettingsDO editModelLightSettings(ModelLightSettingsDO modelLightSettingsDO) throws BusinessException;

    ModelMaterialSettingsDO editModelMaterialSettings(ModelMaterialSettingsDO modelMaterialSettingsDO) throws BusinessException;

    ModelPostprocessingSettingsDO editModelPostprocessingSettings(ModelPostprocessingSettingsDO modelPostprocessingSettingsDO) throws BusinessException;

    void addUserModelOwnershipRelation(String userAddress, Long model, OwnershipTypeEnum ownershipType) throws BusinessException;

    void updateModelOwner(String userAddress, UserModelOwnershipRelationDO beforeRelation) throws BusinessException;

    UserDO getModelTargetUser(Long modelId, Integer ownership) throws BusinessException;

    UserModelOwnershipRelationDO getModelOwnRelation(Long id);
}
