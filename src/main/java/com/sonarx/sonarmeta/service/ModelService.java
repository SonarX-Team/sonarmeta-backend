package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.CreateModelForm;
import com.sonarx.sonarmeta.domain.form.EditModelForm;
import com.sonarx.sonarmeta.domain.model.*;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author hinsliu
* @description 针对表【t_model(模型信息)】的数据库操作Service
* @createDate 2022-08-18 21:40:29
*/
public interface ModelService extends IService<ModelDO> {

    void createModelWithForm(CreateModelForm form);

    void editModelWithForm(EditModelForm form);

    ModelDO getModelById(Long id);

    ModelBasicSettingsDO getModelBasicSettings(Long modelId);

    void editModelBasicSettings(ModelBasicSettingsDO modelBasicSettingsDO);

    ModelLightSettingsDO getModelLightSettings(Long modelId);

    void editModelLightSettings(ModelLightSettingsDO modelLightSettingsDO);

    ModelMaterialSettingsDO getModelMaterialSettings(Long modelId);

    void editModelMaterialSettings(ModelMaterialSettingsDO modelMaterialSettingsDO);

    ModelPostprocessingSettingsDO getModelPostProcessingSettings(Long modelId);

    void editModelPostprocessingSettings(ModelPostprocessingSettingsDO modelPostprocessingSettingsDO);

    void addUserModelOwnershipRelation(String userAddress, Long model, OwnershipTypeEnum ownershipType);

    void updateModelOwner(String userAddress, UserModelOwnershipRelationDO beforeRelation);

    UserModelOwnershipRelationDO getOwnerShipRelationByUserAndModel(String userAddress, Long id);

    UserModelOwnershipRelationDO getModelOwnRelation(Long id);
}
