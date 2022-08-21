package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.BusinessError;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.CreateSceneForm;
import com.sonarx.sonarmeta.domain.form.EditSceneForm;
import com.sonarx.sonarmeta.domain.model.ModelDO;
import com.sonarx.sonarmeta.domain.model.SceneDO;
import com.sonarx.sonarmeta.domain.model.SceneModelRelationDO;
import com.sonarx.sonarmeta.domain.model.UserSceneOwnershipRelationDO;
import com.sonarx.sonarmeta.domain.view.SceneView;
import com.sonarx.sonarmeta.mapper.ModelMapper;
import com.sonarx.sonarmeta.mapper.SceneMapper;
import com.sonarx.sonarmeta.mapper.SceneModelRelationMapper;
import com.sonarx.sonarmeta.mapper.UserSceneOwnershipRelationMapper;
import com.sonarx.sonarmeta.service.SceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
* @author hinsliu
* @description 针对表【t_scene(场景信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/

@Slf4j
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, SceneDO>
    implements SceneService{

    @Resource
    SceneMapper sceneMapper;

    @Resource
    UserSceneOwnershipRelationMapper userSceneOwnershipRelationMapper;

    @Resource
    SceneModelRelationMapper sceneModelRelationMapper;

    @Resource
    ModelMapper modelMapper;

    @Override
    @Transactional
    public void createSceneWithForm(CreateSceneForm createSceneForm) {
        // TODO 创建NFT
        Long nftTokenId = 11111L;

        // 新增场景信息
        SceneDO sceneDO = new SceneDO();
        BeanUtils.copyProperties(createSceneForm, sceneDO);
        sceneDO.setNftTokenId(nftTokenId);
        int affectCount = sceneMapper.insert(sceneDO);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.CREATE_SCENE_ERROR);
        }

        // 新增用户和场景关联信息
        UserSceneOwnershipRelationDO relation = new UserSceneOwnershipRelationDO();
        relation.setSceneId(sceneDO.getId());
        relation.setUserId(createSceneForm.getUserId());
        relation.setOwnershipType(OwnershipTypeEnum.OWN.getCode());
        affectCount = userSceneOwnershipRelationMapper.insert(relation);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.CREATE_USER_AND_SCENE_ERROR);
        }

        log.info("新建场景信息：用户{}，场景{}，NFT{}", relation.getUserId(), relation.getSceneId(), sceneDO.getNftTokenId());
    }

    @Override
    @Transactional
    public void editSceneWithForm(EditSceneForm editSceneForm) {
        QueryWrapper<UserSceneOwnershipRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", editSceneForm.getUserId()).eq("scene_id", editSceneForm.getId());
        UserSceneOwnershipRelationDO relation = userSceneOwnershipRelationMapper.selectOne(queryWrapper);
        if (relation == null) {
            throw new BusinessException(BusinessError.EDIT_SCENE_ERROR);
        }

        SceneDO sceneDO = new SceneDO();
        BeanUtils.copyProperties(editSceneForm, sceneDO);
        int affectCount = sceneMapper.updateById(sceneDO);
        if (affectCount <= 0) {
            throw new BusinessException(BusinessError.EDIT_SCENE_ERROR);
        }

        log.info("编辑模型信息：用户{}，场景{}", relation.getUserId(), relation.getSceneId());
    }

    @Override
    public SceneView getScene(Long sceneId) {
        SceneDO sceneDO = sceneMapper.selectById(sceneId);
        if (sceneDO == null) {
            return null;
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
        return null;
    }
}




