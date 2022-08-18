package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.SceneModelRelationDO;
import com.sonarx.sonarmeta.service.SceneModelRelationService;
import com.sonarx.sonarmeta.mapper.SceneModelRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_scene_model_relation(场景和模型对应关系信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class SceneModelRelationServiceImpl extends ServiceImpl<SceneModelRelationMapper, SceneModelRelationDO>
    implements SceneModelRelationService{

}




