package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.model.UserModelOwnershipRelationDO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author hinsliu
 * @description 针对表【t_user_model_ownership_relation(用户和模型对应关系信息)】的数据库操作Service
 * @createDate 2022-08-18 21:40:29
 */
public interface UserModelOwnershipRelationService extends IService<UserModelOwnershipRelationDO> {

    void addUserModelOwnershipRelation(Long user, Long model, OwnershipTypeEnum ownershipType);

    void updateModelOwner(Long newUser, UserModelOwnershipRelationDO beforeRelation);

    UserModelOwnershipRelationDO getOwnerShipRelationByUserAndModel(Long userId, Long id);

    UserModelOwnershipRelationDO getModelOwnRelation(Long id);
}
