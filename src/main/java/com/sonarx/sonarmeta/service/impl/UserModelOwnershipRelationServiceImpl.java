package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.UserModelOwnershipRelationDO;
import com.sonarx.sonarmeta.service.UserModelOwnershipRelationService;
import com.sonarx.sonarmeta.mapper.UserModelOwnershipRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_user_model_ownership_relation(用户和模型对应关系信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class UserModelOwnershipRelationServiceImpl extends ServiceImpl<UserModelOwnershipRelationMapper, UserModelOwnershipRelationDO>
    implements UserModelOwnershipRelationService{

}




