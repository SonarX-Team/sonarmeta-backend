package com.sonarx.sonarmeta.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sonarx.sonarmeta.domain.form.CreateSceneForm;
import com.sonarx.sonarmeta.domain.form.EditSceneForm;
import com.sonarx.sonarmeta.domain.model.SceneDO;

/**
* @author hinsliu
* @description 针对表【t_scene(场景信息)】的数据库操作Service
* @createDate 2022-08-19 16:49:31
*/
public interface SceneService extends IService<SceneDO> {

    void createSceneWithForm(CreateSceneForm form);

    void editSceneWithForm(EditSceneForm form);

}
