package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.common.PageParam;
import com.sonarx.sonarmeta.domain.enums.BusinessError;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.CreateSceneForm;
import com.sonarx.sonarmeta.domain.form.EditSceneForm;
import com.sonarx.sonarmeta.domain.form.EditSceneModelRelationForm;
import com.sonarx.sonarmeta.domain.model.*;
import com.sonarx.sonarmeta.domain.view.SceneView;
import com.sonarx.sonarmeta.mapper.*;
import com.sonarx.sonarmeta.service.ModelService;
import com.sonarx.sonarmeta.service.SceneService;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.service.Web3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hinsliu
 * @description 针对表【t_scene(场景信息)】的数据库操作Service实现
 * @createDate 2022-08-18 21:40:29
 */

@Slf4j
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, SceneDO>
        implements SceneService {

    @Resource
    SceneMapper sceneMapper;

    @Resource
    UserSceneOwnershipRelationMapper userSceneOwnershipRelationMapper;

    @Resource
    SceneModelRelationMapper sceneModelRelationMapper;

    @Resource
    ModelMapper modelMapper;

    @Resource
    UserModelOwnershipRelationMapper userModelOwnershipRelationMapper;

    @Resource
    Web3Service web3Service;

    @Resource
    UserService userService;

    @Resource
    ModelService modelService;

    @Override
    @Transactional
    public SceneDO createSceneWithForm(CreateSceneForm form) throws BusinessException {
        // 创建NFT
        UserDO user = userService.getById(form.getUserAddress());
        if (user == null) {
            throw new BusinessException(BusinessError.USER_NOT_EXIST_ERROR);
        }
        Long nftTokenId;
        if (form.getModelIdList().size() == 0) {
            nftTokenId = web3Service.mintERC998(form.getUserAddress());
        } else {
            // 获取childTokenIds
            List<Long> childTokenIds = new LinkedList<>();
            for (Long modelId : form.getModelIdList()) {
                ModelDO model = modelMapper.selectById(modelId);
                if (model == null) {
                    throw new BusinessException(BusinessError.MODEL_NOT_EXIST);
                } else {
                    childTokenIds.add(model.getNftTokenId());
                }
            }
            nftTokenId = web3Service.mintERC998WithBatchTokens(form.getUserAddress(), childTokenIds);
        }

        // 新增场景信息
        SceneDO sceneDO = new SceneDO();
        BeanUtils.copyProperties(form, sceneDO);
        sceneDO.setNftTokenId(nftTokenId);
        int affectCount = sceneMapper.insert(sceneDO);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.CREATE_SCENE_ERROR);
        }

        // 新增用户和场景关联信息
        addUserSceneOwnershipRelation(form.getUserAddress(), sceneDO.getId(), OwnershipTypeEnum.SCENE_CREATOR);
        addUserSceneOwnershipRelation(form.getUserAddress(), sceneDO.getId(), OwnershipTypeEnum.SCENE_OWNER);

        for (Long modelId : form.getModelIdList()) {
            UserDO modelCreator = modelService.getModelOwnerOrCreator(modelId, OwnershipTypeEnum.MODEL_CREATOR.getCode());
            UserDO modelOwner = modelService.getModelOwnerOrCreator(modelId, OwnershipTypeEnum.MODEL_OWNER.getCode());
            List<UserDO> modelGrantors = modelService.getModelGrantors(modelId);
            if ((modelCreator != null && form.getUserAddress().equals(modelCreator.getAddress())) ||
                    (modelOwner != null && form.getUserAddress().equals(modelOwner.getAddress())) ||
                    (modelGrantors != null && modelGrantors.stream().anyMatch(modelGrantor -> form.getUserAddress().equals(modelGrantor.getAddress())))
            ) {
                sceneModelRelationMapper.insert(new SceneModelRelationDO(sceneDO.getId(), modelId));
            } else {
                throw new BusinessException(BusinessError.USER_MODEL_PERMISSION_DENIED);
            }
        }

        log.info("新建场景信息：用户{}，场景{}，NFT{}", form.getUserAddress(), sceneDO.getId(), sceneDO.getNftTokenId());
        return sceneDO;
    }

    @Override
    @Transactional
    public SceneDO editSceneWithForm(EditSceneForm form) throws BusinessException {
        QueryWrapper<UserSceneOwnershipRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("address", form.getUserAddress()).eq("scene_id", form.getId()).eq("ownership_type", OwnershipTypeEnum.SCENE_OWNER.getCode());
        UserSceneOwnershipRelationDO relation = userSceneOwnershipRelationMapper.selectOne(queryWrapper);
        if (relation == null) {
            throw new BusinessException(BusinessError.EDIT_SCENE_ERROR);
        }

        SceneDO sceneDO = new SceneDO();
        BeanUtils.copyProperties(form, sceneDO);
        int affectCount = sceneMapper.updateById(sceneDO);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.EDIT_SCENE_ERROR);
        }

        log.info("编辑模型信息：用户{}，场景{}", relation.getAddress(), relation.getSceneId());
        return sceneDO;
    }

    @Override
    public SceneView getScene(Long sceneId) throws BusinessException {
        SceneDO sceneDO = sceneMapper.selectById(sceneId);
        if (sceneDO == null) {
            throw new BusinessException(BusinessError.SCENE_NOT_EXIST);
        }
        SceneView sceneView = new SceneView(sceneDO);
        List<ModelDO> modelList = new LinkedList<>();
        QueryWrapper<SceneModelRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("scene_id", sceneId);
        List<SceneModelRelationDO> sceneModelRelationDOS = sceneModelRelationMapper.selectList(queryWrapper);
        if (sceneModelRelationDOS != null) {
            for (SceneModelRelationDO sceneModelRelationDO : sceneModelRelationDOS) {
                modelList.add(modelMapper.selectById(sceneModelRelationDO.getModelId()));
            }
        }
        sceneView.setModelList(modelList);
        UserDO sceneCreator = getSceneOwnerOrCreator(sceneId, OwnershipTypeEnum.SCENE_CREATOR.getCode());
        sceneView.setCreatorAddress(sceneCreator.getAddress());
        sceneView.setCreatorUsername(sceneCreator.getUsername());
        sceneView.setCreatorAvatar(sceneCreator.getAvatar());
        UserDO sceneOwner = getSceneOwnerOrCreator(sceneId, OwnershipTypeEnum.SCENE_CREATOR.getCode());
        sceneView.setOwnerAddress(sceneOwner.getAddress());
        sceneView.setOwnerUsername(sceneOwner.getUsername());
        sceneView.setOwnerAvatar(sceneOwner.getAvatar());
        return sceneView;
    }

    @Override
    public void editSceneModelRelation(EditSceneModelRelationForm sceneModelRelation) throws BusinessException {

        //拥有场景的用户才能更新场景与模型的关系
        QueryWrapper<UserSceneOwnershipRelationDO> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("address", sceneModelRelation.getUserAddress())
                .eq("scene_id", sceneModelRelation.getSceneId());
        UserSceneOwnershipRelationDO userSceneOwnershipRelationDO = userSceneOwnershipRelationMapper.selectOne(queryWrapper1);
        if (userSceneOwnershipRelationDO == null || !userSceneOwnershipRelationDO.getOwnershipType().equals(OwnershipTypeEnum.SCENE_OWNER.getCode())) {
            throw new BusinessException(BusinessError.USER_SCENE_PERMISSION_DENIED);
        }

        //该用户必须同时具备对应模型的拥有权或授予权
        for (Long modelId : sceneModelRelation.getModelIdList()) {
            QueryWrapper<UserModelOwnershipRelationDO> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("address", sceneModelRelation.getUserAddress())
                    .eq("model_id", modelId);
            UserModelOwnershipRelationDO userModelOwnershipRelationDO = userModelOwnershipRelationMapper.selectOne(queryWrapper2);
            if (userModelOwnershipRelationDO == null) {
                throw new BusinessException(BusinessError.USER_MODEL_PERMISSION_DENIED);
            }
        }

        //删除原有scene的关系
        QueryWrapper<SceneModelRelationDO> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("scene_id", sceneModelRelation.getSceneId());
        sceneModelRelationMapper.delete(queryWrapper3);

        //新增关系
        for (Long modelId : sceneModelRelation.getModelIdList()) {
            sceneModelRelationMapper.insert(new SceneModelRelationDO(sceneModelRelation.getSceneId(), modelId));
        }

    }

    @Override
    public UserSceneOwnershipRelationDO getSceneOwnRelation(Long id) {
        return userSceneOwnershipRelationMapper.selectOne(
                new QueryWrapper<UserSceneOwnershipRelationDO>()
                        .eq("scene_id", id)
                        .eq("ownership_type", OwnershipTypeEnum.SCENE_OWNER.getCode())
        );
    }

    @Override
    public void addUserSceneOwnershipRelation(String userAddress, Long sceneId, OwnershipTypeEnum ownershipType) throws BusinessException {
        QueryWrapper<UserSceneOwnershipRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("address", userAddress)
                .eq("scene_id", sceneId)
                .eq("ownership_type", ownershipType);
        if (userSceneOwnershipRelationMapper.selectOne(queryWrapper) == null) {
            int affectCount = userSceneOwnershipRelationMapper.insert(new UserSceneOwnershipRelationDO(userAddress, sceneId, ownershipType.getCode()));
            if (affectCount <= 0) {
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "新增模型" + ownershipType.getDesc() + "权限失败");
            }
        }
    }

    @Override
    public UserDO getSceneOwnerOrCreator(Long sceneId, Integer ownership) {
        if (!ownership.equals(OwnershipTypeEnum.SCENE_OWNER.getCode()) && !ownership.equals(OwnershipTypeEnum.SCENE_CREATOR.getCode())) {
            return null;
        }

        UserDO userDO = null;
        UserSceneOwnershipRelationDO relationDO = userSceneOwnershipRelationMapper.selectOne(
                new QueryWrapper<UserSceneOwnershipRelationDO>()
                        .eq("scene_id", sceneId)
                        .eq("ownership_type", ownership));
        if (relationDO != null) {
            userDO = userService.getById(relationDO.getAddress());
        }
        return userDO;
    }

    @Override
    public List<UserDO> getSceneDivers(Long sceneId) {
        UserDO userDO = null;
        List<UserSceneOwnershipRelationDO> relationDOs = userSceneOwnershipRelationMapper.selectList(
                new QueryWrapper<UserSceneOwnershipRelationDO>()
                        .eq("scene_id", sceneId)
                        .eq("ownership_type", OwnershipTypeEnum.SCENE_DIVER.getCode()));
        if (relationDOs != null) {
            return relationDOs.stream().map(
                    relationDO -> userService.getById(relationDO.getAddress())
            ).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void updateSceneOwner(String userAddress, UserSceneOwnershipRelationDO beforeRelation) throws BusinessException {
        UpdateWrapper<UserSceneOwnershipRelationDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("scene_id", beforeRelation.getSceneId())
                .eq("ownership_type", beforeRelation.getOwnershipType());
        UserSceneOwnershipRelationDO userSceneOwnershipRelationDO = new UserSceneOwnershipRelationDO(userAddress, beforeRelation.getSceneId(), beforeRelation.getOwnershipType());
        int affectCount = userSceneOwnershipRelationMapper.update(userSceneOwnershipRelationDO, updateWrapper);
        if (affectCount <= 0) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "场景拥有权转让失败");
        }
    }
}




