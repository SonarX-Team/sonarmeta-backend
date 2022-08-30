package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.*;
import com.sonarx.sonarmeta.domain.common.PageWrapper;
import com.sonarx.sonarmeta.domain.common.PageParam;
import com.sonarx.sonarmeta.domain.model.*;
import com.sonarx.sonarmeta.domain.query.AllWorksListQuery;
import com.sonarx.sonarmeta.domain.query.SearchWorksListQuery;
import com.sonarx.sonarmeta.domain.view.WorksView;
import com.sonarx.sonarmeta.mapper.*;
import com.sonarx.sonarmeta.service.ModelService;
import com.sonarx.sonarmeta.service.SceneService;
import com.sonarx.sonarmeta.service.WorksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @description: Implement WorksService
 * @author: liuxuanming
 */
@Slf4j
@Service
public class WorksServiceImpl implements WorksService {

    @Resource
    UserMapper userMapper;

    @Resource
    ModelMapper modelMapper;

    @Resource
    SceneMapper sceneMapper;

    @Resource
    UserModelOwnershipRelationMapper userModelOwnershipRelationMapper;

    @Resource
    UserSceneOwnershipRelationMapper userSceneOwnershipRelationMapper;

    @Resource
    ModelService modelService;

    @Resource
    SceneService sceneService;

    @Override
    public PageWrapper<WorksView> getWorksList(AllWorksListQuery query) {
        List<WorksView> result = new LinkedList<>();
        // 分页验证
        PageParam.verify(query);

        // 获取Models
        Page<ModelDO> pageSelector1 = new Page<>(query.getPage(), query.getPageSize() / 2);
        QueryWrapper<ModelDO> qw1 = new QueryWrapper<>();
        qw1.eq("status", ModelStatusEnum.MODEL_STATUS_PASSED.getStatusCode());
        Page<ModelDO> models = modelMapper.selectPage(pageSelector1, qw1);
        models.getRecords().forEach(modelDO -> {
            UserModelOwnershipRelationDO modelOwnRelation = modelService.getModelOwnRelation(modelDO.getId());
            UserDO userDO = userMapper.selectById(modelOwnRelation.getAddress());
            result.add(getWorksFromModel(modelDO, userDO));
        });

        // 获取Scenes
        Page<SceneDO> pageSelector2 = new Page<>(query.getPage(), query.getPageSize() / 2);
        QueryWrapper<SceneDO> qw2 = new QueryWrapper<>();
        qw2.eq("status", SceneStatusEnum.SCENE_STATUS_PASSED.getStatusCode());
        Page<SceneDO> scenes = sceneMapper.selectPage(pageSelector2, qw2);
        scenes.getRecords().forEach(sceneDO -> {
            UserSceneOwnershipRelationDO sceneOwnershipRelationDO = sceneService.getUserSceneRelation(sceneDO.getId());
            UserDO userDO = userMapper.selectById(sceneOwnershipRelationDO.getAddress());
            result.add(getWorksFromScene(sceneDO, userDO));
        });

        return new PageWrapper<>(query.getPage(), query.getPageSize(), result.size(), result);
    }

    @Override
    public PageWrapper<WorksView> searchWorksList(SearchWorksListQuery query) {
        List<WorksView> result = new LinkedList<>();
        // 分页验证
        PageParam.verify(query);

        // 获取Models
        Page<ModelDO> pageSelector1 = new Page<>(query.getPage(), query.getPageSize() / 2);
        QueryWrapper<ModelDO> qw1 = new QueryWrapper<>();
        qw1.eq("status", ModelStatusEnum.MODEL_STATUS_PASSED.getStatusCode())
                .and(w -> {
                    if (query.getTitle() != null) {
                        w.like("title", query.getTitle());
                    } else if (query.getTags() != null) {
                        w.eq("tags", query.getTags());
                    }
                });
        Page<ModelDO> models = modelMapper.selectPage(pageSelector1, qw1);
        models.getRecords().forEach(modelDO -> {
            UserModelOwnershipRelationDO modelOwnRelation = modelService.getModelOwnRelation(modelDO.getId());
            UserDO userDO = userMapper.selectById(modelOwnRelation.getAddress());
            result.add(getWorksFromModel(modelDO, userDO));
        });

        // 获取Scenes
        Page<SceneDO> pageSelector2 = new Page<>(query.getPage(), query.getPageSize() / 2);
        QueryWrapper<SceneDO> qw2 = new QueryWrapper<>();
        qw2.eq("status", SceneStatusEnum.SCENE_STATUS_PASSED.getStatusCode())
                .and(w -> {
                    if (query.getTitle() != null) {
                        w.like("title", query.getTitle());
                    } else if (query.getTags() != null) {
                        w.eq("tags", query.getTags());
                    }
                });
        Page<SceneDO> scenes = sceneMapper.selectPage(pageSelector2, qw2);
        scenes.getRecords().forEach(sceneDO -> {
            UserSceneOwnershipRelationDO sceneOwnershipRelationDO = sceneService.getUserSceneRelation(sceneDO.getId());
            UserDO userDO = userMapper.selectById(sceneOwnershipRelationDO.getAddress());
            result.add(getWorksFromScene(sceneDO, userDO));
        });

        return new PageWrapper<>(query.getPage(), query.getPageSize(), result.size(), result);
    }

    @Override
    public Map<Integer, List<WorksView>> getWorksByUserAddress(String userAddress) throws BusinessException {

        UserDO userDO = userMapper.selectById(userAddress);
        if (userDO == null) {
            throw new BusinessException(BusinessError.USER_NOT_EXIST_ERROR);
        }

        Map<Integer, List<WorksView>> res = new HashMap<>();
        List<WorksView> createModelList = new LinkedList<>();
        List<WorksView> ownModelList = new LinkedList<>();
        List<WorksView> grantModelList = new LinkedList<>();
        List<WorksView> createSceneList = new LinkedList<>();
        List<WorksView> ownSceneList = new LinkedList<>();
        List<WorksView> diveSceneList = new LinkedList<>();

        // 根据user address查询用户models
        QueryWrapper<UserModelOwnershipRelationDO> userModelOwnershipRelationDOQueryWrapper = new QueryWrapper<>();
        userModelOwnershipRelationDOQueryWrapper.eq("address", userAddress);
        List<UserModelOwnershipRelationDO> userModelOwnershipRelationDOS = userModelOwnershipRelationMapper.selectList(userModelOwnershipRelationDOQueryWrapper);
        if (userModelOwnershipRelationDOS != null) {
            for (UserModelOwnershipRelationDO userModelOwnershipRelationDO : userModelOwnershipRelationDOS) {
                if (userModelOwnershipRelationDO.getOwnershipType().equals(OwnershipTypeEnum.MODEL_CREATOR.getCode())) {
                    ModelDO modelDO = modelMapper.selectById(userModelOwnershipRelationDO.getModelId());
                    if (modelDO != null) {
                        createModelList.add(getWorksFromModel(modelDO, userDO));
                    }
                }
                if (userModelOwnershipRelationDO.getOwnershipType().equals(OwnershipTypeEnum.MODEL_OWNER.getCode())) {
                    ModelDO modelDO = modelMapper.selectById(userModelOwnershipRelationDO.getModelId());
                    if (modelDO != null) {
                        ownModelList.add(getWorksFromModel(modelDO, userDO));
                    }
                }
                if (userModelOwnershipRelationDO.getOwnershipType().equals(OwnershipTypeEnum.MODEL_GRANTOR.getCode())) {
                    ModelDO modelDO = modelMapper.selectById(userModelOwnershipRelationDO.getModelId());
                    if (modelDO != null) {
                        grantModelList.add(getWorksFromModel(modelDO, userDO));
                    }
                }
            }
        }

        // 根据user address查询用户scenes
        QueryWrapper<UserSceneOwnershipRelationDO> userSceneOwnershipRelationDOQueryWrapper = new QueryWrapper<>();
        userSceneOwnershipRelationDOQueryWrapper.eq("address", userAddress);
        List<UserSceneOwnershipRelationDO> userSceneOwnershipRelationDOS = userSceneOwnershipRelationMapper.selectList(userSceneOwnershipRelationDOQueryWrapper);
        if (userSceneOwnershipRelationDOS != null) {
            for (UserSceneOwnershipRelationDO userSceneOwnershipRelationDO : userSceneOwnershipRelationDOS) {
                if (userSceneOwnershipRelationDO.getOwnershipType().equals(OwnershipTypeEnum.SCENE_CREATOR.getCode())) {
                    SceneDO sceneDO = sceneMapper.selectById(userSceneOwnershipRelationDO.getSceneId());
                    if (sceneDO != null) {
                        createSceneList.add(getWorksFromScene(sceneDO, userDO));
                    }
                }
                if (userSceneOwnershipRelationDO.getOwnershipType().equals(OwnershipTypeEnum.SCENE_OWNER.getCode())) {
                    SceneDO sceneDO = sceneMapper.selectById(userSceneOwnershipRelationDO.getSceneId());
                    if (sceneDO != null) {
                        ownSceneList.add(getWorksFromScene(sceneDO, userDO));
                    }
                }
                if (userSceneOwnershipRelationDO.getOwnershipType().equals(OwnershipTypeEnum.SCENE_DIVER.getCode())) {
                    SceneDO sceneDO = sceneMapper.selectById(userSceneOwnershipRelationDO.getSceneId());
                    if (sceneDO != null) {
                        diveSceneList.add(getWorksFromScene(sceneDO, userDO));
                    }
                }
            }
        }
        res.put(OwnershipTypeEnum.MODEL_CREATOR.getCode(), createModelList);
        res.put(OwnershipTypeEnum.MODEL_OWNER.getCode(), ownModelList);
        res.put(OwnershipTypeEnum.MODEL_GRANTOR.getCode(), grantModelList);
        res.put(OwnershipTypeEnum.SCENE_CREATOR.getCode(), createSceneList);
        res.put(OwnershipTypeEnum.SCENE_OWNER.getCode(), ownSceneList);
        res.put(OwnershipTypeEnum.SCENE_DIVER.getCode(), diveSceneList);
        return res;

    }

    private WorksView getWorksFromScene(SceneDO sceneDO, UserDO userDO) {
        WorksView worksView = new WorksView();
        worksView.setCover(sceneDO.getCover());
        worksView.setCreateTime(sceneDO.getCreateTime());
        worksView.setPrice(sceneDO.getDivePrice());
        worksView.setId(sceneDO.getId());
        worksView.setTitle(sceneDO.getTitle());
        worksView.setAvatar(userDO.getAvatar());
        worksView.setUsername(userDO.getUsername());
        worksView.setWorksType(WorksTypeEnum.WORKS_SCENE.getCode());
        return worksView;
    }

    private WorksView getWorksFromModel(ModelDO modelDO, UserDO userDO) {
        WorksView worksView = new WorksView();
        worksView.setCover(modelDO.getCover());
        worksView.setCreateTime(modelDO.getCreateTime());
        worksView.setPrice(modelDO.getGrantPrice());
        worksView.setId(modelDO.getId());
        worksView.setTitle(modelDO.getTitle());
        worksView.setAvatar(userDO.getAvatar());
        worksView.setUsername(userDO.getUsername());
        worksView.setWorksType(WorksTypeEnum.WORKS_MODEL.getCode());
        return worksView;
    }
}
