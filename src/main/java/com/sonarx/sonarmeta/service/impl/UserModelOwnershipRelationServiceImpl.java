package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.model.UserModelOwnershipRelationDO;
import com.sonarx.sonarmeta.service.UserModelOwnershipRelationService;
import com.sonarx.sonarmeta.mapper.UserModelOwnershipRelationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.primitive.Int;

import javax.annotation.Resource;

/**
* @author hinsliu
* @description 针对表【t_user_model_ownership_relation(用户和模型对应关系信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class UserModelOwnershipRelationServiceImpl extends ServiceImpl<UserModelOwnershipRelationMapper, UserModelOwnershipRelationDO>
    implements UserModelOwnershipRelationService{

    @Resource
    UserModelOwnershipRelationMapper userModelOwnershipRelationMapper;

    public void addUserModelOwnershipRelation(Long user, Long model, OwnershipTypeEnum ownershipType) {
        UserModelOwnershipRelationDO relation = new UserModelOwnershipRelationDO();
        relation.setUserId(user);
        relation.setModelId(model);
        relation.setOwnershipType(ownershipType.getCode());
        int affectCount = userModelOwnershipRelationMapper.insert(relation);
        if (affectCount <= 0) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "新增模型" + ownershipType.getDesc() + "权限失败");
        }
    }

    @Override
    public void updateModelOwner(Long newUser, UserModelOwnershipRelationDO beforeRelation) {
        UserModelOwnershipRelationDO afterRelation = new UserModelOwnershipRelationDO();
        BeanUtils.copyProperties(beforeRelation, afterRelation);
        afterRelation.setUserId(newUser);
        int affectCount = userModelOwnershipRelationMapper.updateById(afterRelation);
        if (affectCount <= 0) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "模型拥有权转让失败");
        }
    }

    @Override
    public UserModelOwnershipRelationDO getOwnerShipRelationByUserAndModel(Long userId, Long id) {
        return userModelOwnershipRelationMapper.selectOne(
                new QueryWrapper<UserModelOwnershipRelationDO>()
                        .eq("model_id", id)
                        .eq("user_id", userId)
        );
    }

    @Override
    public UserModelOwnershipRelationDO getModelOwnRelation(Long id) {
        return userModelOwnershipRelationMapper.selectOne(
                new QueryWrapper<UserModelOwnershipRelationDO>()
                        .eq("model_id", id)
                        .eq("ownership_type", OwnershipTypeEnum.OWN.getCode())
        );
    }

}




