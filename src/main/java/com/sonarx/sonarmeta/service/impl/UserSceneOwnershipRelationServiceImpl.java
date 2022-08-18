package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.UserSceneOwnershipRelationDO;
import com.sonarx.sonarmeta.service.UserSceneOwnershipRelationService;
import com.sonarx.sonarmeta.mapper.UserSceneOwnershipRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_user_scene_ownership_relation(用户和场景对应关系信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class UserSceneOwnershipRelationServiceImpl extends ServiceImpl<UserSceneOwnershipRelationMapper, UserSceneOwnershipRelationDO>
    implements UserSceneOwnershipRelationService{

}




