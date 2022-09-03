package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.BusinessError;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.CreateModelForm;
import com.sonarx.sonarmeta.domain.form.EditModelForm;
import com.sonarx.sonarmeta.domain.model.*;
import com.sonarx.sonarmeta.domain.view.ModelView;
import com.sonarx.sonarmeta.mapper.*;
import com.sonarx.sonarmeta.service.ModelService;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.service.Web3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hinsliu
 * @description 针对表【t_model(模型信息)】的数据库操作Service实现
 * @createDate 2022-08-18 21:40:29
 */
@Slf4j
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, ModelDO>
        implements ModelService {

    @Resource
    ModelMapper modelMapper;

    @Resource
    UserModelOwnershipRelationMapper userModelOwnershipRelationMapper;

    @Resource
    ModelBasicSettingsMapper modelBasicSettingsMapper;

    @Resource
    ModelLightSettingsMapper modelLightSettingsMapper;

    @Resource
    ModelMaterialSettingsMapper modelMaterialSettingsMapper;

    @Resource
    ModelPostprocessingSettingsMapper modelPostprocessingSettingsMapper;

    @Resource
    Web3Service web3Service;

    @Resource
    UserService userService;

    @Override
    @Transactional
    public ModelDO createModelWithForm(CreateModelForm form) throws BusinessException {
        // 创建NFT
        UserDO user = userService.getById(form.getUserAddress());
        if(user == null) {
            throw new BusinessException(BusinessError.USER_NOT_EXIST_ERROR);
        }
        Long nftTokenId = web3Service.mintERC721(user.getAddress());

        // 新增模型信息
        ModelDO model = new ModelDO();
        BeanUtils.copyProperties(form, model);
        model.setNftTokenId(nftTokenId);
        int affectCount = modelMapper.insert(model);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.CREATE_MODEL_ERROR);
        }
        // 新增用户和模型关联信息
        addUserModelOwnershipRelation(form.getUserAddress(), model.getId(), OwnershipTypeEnum.MODEL_CREATOR);
        addUserModelOwnershipRelation(form.getUserAddress(), model.getId(), OwnershipTypeEnum.MODEL_OWNER);
        log.info("新建模型信息：用户{}，模型{}，NFT{}", form.getUserAddress(), model.getId(), model.getNftTokenId());
        return model;
    }

    @Override
    @Transactional
    public ModelDO editModelWithForm(EditModelForm form) throws BusinessException {
        QueryWrapper<UserModelOwnershipRelationDO> qw = new QueryWrapper<>();
        qw.eq("address", form.getUserAddress()).eq("model_id", form.getId()).eq("ownership_type",OwnershipTypeEnum.MODEL_OWNER.getCode());
        UserModelOwnershipRelationDO relation = userModelOwnershipRelationMapper.selectOne(qw);
        if (relation == null) {
            throw new BusinessException(BusinessError.EDIT_MODEL_ERROR);
        }
        ModelDO model = new ModelDO();
        BeanUtils.copyProperties(form, model);
        int affectCount = modelMapper.updateById(model);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.EDIT_MODEL_ERROR);
        }
        log.info("编辑模型信息：用户{}，模型{}", relation.getAddress(), relation.getModelId());
        return model;
    }

    @Override
    public ModelDO getModelById(Long id) throws BusinessException {
        ModelDO modelDO = modelMapper.selectById(id);
        if (modelDO == null) {
            throw new BusinessException(BusinessError.MODEL_NOT_EXIST);
        }
        return modelDO;
    }

    @Override
    public ModelView getModelViewById(Long id) throws BusinessException {
        ModelView modelView = new ModelView();
        ModelDO modelDO = modelMapper.selectById(id);
        if (modelDO == null) {
            throw new BusinessException(BusinessError.MODEL_NOT_EXIST);
        }
        BeanUtils.copyProperties(modelDO, modelView);

        UserDO owner = getModelTargetUser(id, OwnershipTypeEnum.MODEL_OWNER.getCode());
        modelView.setOwnerAvatar(owner.getAvatar());
        modelView.setOwnerUsername(owner.getUsername());
        modelView.setOwnerAddress(owner.getAddress());

        UserDO creator = getModelTargetUser(id, OwnershipTypeEnum.MODEL_CREATOR.getCode());
        modelView.setCreatorAvatar(creator.getAvatar());
        modelView.setCreatorUsername(creator.getUsername());
        modelView.setCreatorAddress(creator.getAddress());
        return modelView;

    }


    @Override
    public ModelBasicSettingsDO editModelBasicSettings(ModelBasicSettingsDO modelBasicSettingsDO) throws BusinessException {
        QueryWrapper<ModelBasicSettingsDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model_id", modelBasicSettingsDO.getModelId());
        if (modelBasicSettingsMapper.selectOne(queryWrapper) == null) {
            modelBasicSettingsMapper.insert(modelBasicSettingsDO);
            return modelBasicSettingsDO;
        }
        UpdateWrapper<ModelBasicSettingsDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("model_id", modelBasicSettingsDO.getModelId());
        int affectCount = modelBasicSettingsMapper.update(modelBasicSettingsDO, updateWrapper);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.EDIT_MODEL_ERROR);
        }
        log.info("编辑模型信息：模型{}", modelBasicSettingsDO.getModelId());
        return modelBasicSettingsDO;
    }

    @Override
    public Map<String, Object> getModelDetailSettings(Long modelId) throws BusinessException {

        ModelDO modelDO = modelMapper.selectById(modelId);
        if (modelDO == null) {
            throw new BusinessException(BusinessError.MODEL_NOT_EXIST);
        }

        Map<String, Object> map = new HashMap<>();

        QueryWrapper<ModelBasicSettingsDO> qw1 = new QueryWrapper<>();
        qw1.eq("model_id", modelId);
        map.put("ModelBasicSetting", modelBasicSettingsMapper.selectOne(qw1));

        QueryWrapper<ModelLightSettingsDO> qw2 = new QueryWrapper<>();
        qw2.eq("model_id", modelId);
        map.put("ModelLightSetting", modelLightSettingsMapper.selectOne(qw2));

        QueryWrapper<ModelMaterialSettingsDO> qw3 = new QueryWrapper<>();
        qw3.eq("model_id", modelId);
        map.put("ModelMaterialSetting", modelMaterialSettingsMapper.selectOne(qw3));

        QueryWrapper<ModelPostprocessingSettingsDO> qw4 = new QueryWrapper<>();
        qw4.eq("model_id", modelId);
        map.put("ModelPostprocessingSetting", modelPostprocessingSettingsMapper.selectOne(qw4));

        return map;
    }


    @Override
    public ModelLightSettingsDO editModelLightSettings(ModelLightSettingsDO modelLightSettingsDO) throws BusinessException {
        QueryWrapper<ModelLightSettingsDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model_id", modelLightSettingsDO.getModelId());
        if (modelLightSettingsMapper.selectOne(queryWrapper) == null) {
           modelLightSettingsMapper.insert(modelLightSettingsDO);
           return modelLightSettingsDO;
        }
        UpdateWrapper<ModelLightSettingsDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("model_id", modelLightSettingsDO.getModelId());
        int affectCount = modelLightSettingsMapper.update(modelLightSettingsDO, updateWrapper);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.EDIT_MODEL_ERROR);
        }
        log.info("编辑模型信息：模型{}", modelLightSettingsDO.getModelId());
        return modelLightSettingsDO;
    }


    @Override
    public ModelMaterialSettingsDO editModelMaterialSettings(ModelMaterialSettingsDO modelMaterialSettingsDO) throws BusinessException {
        QueryWrapper<ModelMaterialSettingsDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model_id", modelMaterialSettingsDO.getModelId());
        if (modelMaterialSettingsMapper.selectOne(queryWrapper) == null) {
            modelMaterialSettingsMapper.insert(modelMaterialSettingsDO);
            return modelMaterialSettingsDO;
        }
        UpdateWrapper<ModelMaterialSettingsDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("model_id", modelMaterialSettingsDO.getModelId());
        int affectCount = modelMaterialSettingsMapper.update(modelMaterialSettingsDO, updateWrapper);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.EDIT_MODEL_ERROR);
        }
        log.info("编辑模型信息：模型{}", modelMaterialSettingsDO.getModelId());
        return modelMaterialSettingsDO;
    }


    @Override
    public ModelPostprocessingSettingsDO editModelPostprocessingSettings(ModelPostprocessingSettingsDO modelPostprocessingSettingsDO) throws BusinessException {
        QueryWrapper<ModelPostprocessingSettingsDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model_id", modelPostprocessingSettingsDO.getModelId());
        if (modelPostprocessingSettingsMapper.selectOne(queryWrapper) == null) {
            modelPostprocessingSettingsMapper.insert(modelPostprocessingSettingsDO);
            return modelPostprocessingSettingsDO;
        }
        UpdateWrapper<ModelPostprocessingSettingsDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("model_id", modelPostprocessingSettingsDO.getModelId());
        int affectCount = modelPostprocessingSettingsMapper.update(modelPostprocessingSettingsDO, updateWrapper);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.EDIT_MODEL_ERROR);
        }
        log.info("编辑模型信息：模型{}", modelPostprocessingSettingsDO.getModelId());
        return modelPostprocessingSettingsDO;
    }
    

    @Override
    public void addUserModelOwnershipRelation(String userAddress, Long modelId, OwnershipTypeEnum ownershipType) throws BusinessException {
        QueryWrapper<UserModelOwnershipRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("address",userAddress)
                .eq("model_id",modelId)
                .eq("ownership_type", ownershipType);
        if (userModelOwnershipRelationMapper.selectOne(queryWrapper) == null) {
            int affectCount = userModelOwnershipRelationMapper.insert(new UserModelOwnershipRelationDO(userAddress, modelId, ownershipType.getCode()));
            if (affectCount <= 0) {
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "新增模型" + ownershipType.getDesc() + "权限失败");
            }
        }
    }

    @Override
    public void updateModelOwner(String userAddress, UserModelOwnershipRelationDO beforeRelation) throws BusinessException {
        UpdateWrapper<UserModelOwnershipRelationDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("model_id", beforeRelation.getModelId())
                .eq("ownership_type", beforeRelation.getOwnershipType());
        UserModelOwnershipRelationDO userModelOwnershipRelationDO = new UserModelOwnershipRelationDO(userAddress, beforeRelation.getModelId(), beforeRelation.getOwnershipType());
        int affectCount = userModelOwnershipRelationMapper.update(userModelOwnershipRelationDO, updateWrapper);
        if (affectCount <= 0) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "模型拥有权转让失败");
        }
    }

    @Override
    public UserDO getModelTargetUser(Long modelId, Integer ownership) throws BusinessException {
        UserDO userDO = null;
        UserModelOwnershipRelationDO relationDO = userModelOwnershipRelationMapper.selectOne(
                new QueryWrapper<UserModelOwnershipRelationDO>()
                        .eq("model_id", modelId)
                        .eq("ownership_type", ownership));
        if (relationDO != null) {
            userDO = userService.getById(relationDO.getAddress());
        }
        return userDO;
    }

    @Override
    public UserModelOwnershipRelationDO getModelOwnRelation(Long id) {
        return userModelOwnershipRelationMapper.selectOne(
                new QueryWrapper<UserModelOwnershipRelationDO>()
                        .eq("model_id", id)
                        .eq("ownership_type", OwnershipTypeEnum.MODEL_OWNER.getCode())
        );
    }
}




