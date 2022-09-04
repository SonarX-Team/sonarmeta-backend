package com.sonarx.sonarmeta.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.form.CreateSceneForm;
import com.sonarx.sonarmeta.domain.form.EditSceneForm;
import com.sonarx.sonarmeta.domain.form.EditSceneModelRelationForm;
import com.sonarx.sonarmeta.domain.model.SceneDO;
import com.sonarx.sonarmeta.domain.model.UserDO;
import com.sonarx.sonarmeta.domain.model.UserSceneOwnershipRelationDO;
import com.sonarx.sonarmeta.domain.view.SceneView;

/**
* @author hinsliu
* @description 针对表【t_scene(场景信息)】的数据库操作Service
* @createDate 2022-08-19 16:49:31
*/
public interface SceneService extends IService<SceneDO> {

    SceneDO createSceneWithForm(CreateSceneForm form) throws BusinessException;

    SceneDO editSceneWithForm(EditSceneForm form) throws BusinessException;

    SceneView getScene(Long sceneId) throws BusinessException;

    void editSceneModelRelation(EditSceneModelRelationForm sceneModelRelation) throws BusinessException;

    UserSceneOwnershipRelationDO getUserSceneRelation(Long id);

    UserDO getSceneTargetUser(Long sceneId, Integer ownership);
}
