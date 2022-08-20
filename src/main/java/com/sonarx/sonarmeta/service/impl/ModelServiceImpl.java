package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.CreateModelForm;
import com.sonarx.sonarmeta.domain.form.EditModelForm;
import com.sonarx.sonarmeta.domain.model.ModelDO;
import com.sonarx.sonarmeta.domain.model.UserModelOwnershipRelationDO;
import com.sonarx.sonarmeta.mapper.UserModelOwnershipRelationMapper;
import com.sonarx.sonarmeta.service.ModelService;
import com.sonarx.sonarmeta.mapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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

    @Override
    @Transactional
    public void createModelWithForm(CreateModelForm form) {
        // TODO 创建NFT
        Long nftTokenId = 11111L;

        // 新增模型信息
        ModelDO model = new ModelDO();
        BeanUtils.copyProperties(form, model);
        model.setNftTokenId(nftTokenId);
        int affectCount = modelMapper.insert(model);
        if (affectCount <= 0) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "新建模型信息失败");
        }
        // 新增用户和模型关联信息
        UserModelOwnershipRelationDO relation = new UserModelOwnershipRelationDO();
        relation.setModelId(model.getId());
        relation.setUserId(form.getUserId());
        relation.setOwnershipType(OwnershipTypeEnum.OWN.getCode());
        affectCount = userModelOwnershipRelationMapper.insert(relation);
        if (affectCount <= 0) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "新建用户和模型关系信息失败");
        }

        log.info("新建模型信息：用户{}，模型{}，NFT{}", relation.getUserId(), relation.getModelId(), model.getNftTokenId());
    }

    @Override
    @Transactional
    public void editModelWithForm(EditModelForm form) {
        QueryWrapper<UserModelOwnershipRelationDO> qw = new QueryWrapper<>();
        qw.eq("user_id", form.getUserId()).eq("model_id", form.getId());
        UserModelOwnershipRelationDO relation = userModelOwnershipRelationMapper.selectOne(qw);
        if (relation == null) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "该用户无对应模型的编辑权");
        }

        ModelDO model = new ModelDO();
        BeanUtils.copyProperties(form, model);
        int affectCount = modelMapper.updateById(model);
        if (affectCount <= 0) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "更新模型信息失败");
        }

        log.info("编辑模型信息：用户{}，模型{}", relation.getUserId(), relation.getModelId());
    }

    @Override
    public ModelDO getModelById(Long id) {
        return modelMapper.selectById(id);
    }
}




