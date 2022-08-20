package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.ConsumeTypeEnum;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.ConsumeActionForm;
import com.sonarx.sonarmeta.domain.model.ModelDO;
import com.sonarx.sonarmeta.domain.model.UserDO;
import com.sonarx.sonarmeta.domain.model.UserModelOwnershipRelationDO;
import com.sonarx.sonarmeta.mapper.ModelMapper;
import com.sonarx.sonarmeta.mapper.SceneMapper;
import com.sonarx.sonarmeta.mapper.UserModelOwnershipRelationMapper;
import com.sonarx.sonarmeta.service.ModelService;
import com.sonarx.sonarmeta.service.UserModelOwnershipRelationService;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.sonarx.sonarmeta.common.Constants.APP_NAME;
import static com.sonarx.sonarmeta.common.Constants.DEFAULT_CONNECTOR;

/**
 * @author hinsliu
 * @description 针对表【t_user(用户信息)】的数据库操作Service实现
 * @createDate 2022-08-18 21:40:29
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    ModelMapper modelMapper;

    @Resource
    SceneMapper sceneMapper;

    @Resource
    UserModelOwnershipRelationMapper userModelOwnershipRelationMapper;

    @Resource
    UserModelOwnershipRelationService userModelOwnershipRelationService;

    @Resource
    ModelService modelService;

    public UserDO getOrCreateUserProfileByAddress(String address) {
        // 根据address查询用户信息
        QueryWrapper<UserDO> qw = new QueryWrapper<>();
        qw.select().eq("address", address);
        UserDO user = userMapper.selectOne(qw);

        if (user != null) {
            log.info("获取用户信息，id：{}，钱包地址：{}", user.getId(), user.getAddress());
            return user;
        } else {
            // 如果不存在，则创建新的用户
            UserDO newUser = new UserDO();
            newUser.setAddress(address);
            newUser.setUsername(APP_NAME + DEFAULT_CONNECTOR + address);
            int affectCount = userMapper.insert(newUser);
            if (affectCount > 0) {
                log.info("插入用户信息成功，钱包地址：{}", newUser.getAddress());
                newUser = userMapper.selectById(newUser.getId());
                log.info("获取用户信息，id：{}，钱包地址：{}", newUser.getId(), newUser.getAddress());
            } else {
                log.warn("插入用户信息失败，钱包地址：{}", newUser.getAddress());
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "插入用户信息失败");
            }
            return newUser;
        }
    }

    @Override
    public UserDO getUserProfileById(String id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据不同的消费类型处理
     *
     * @param form
     */
    @Override
    @Transactional
    public void consume(ConsumeActionForm form) {
        Integer consumeType = form.getType();

        if (consumeType == ConsumeTypeEnum.CONSUME_PURCHASE_MODEL.getCode()) {
            // 购买模型
            // 获取用户和该对象的所属关系
            UserModelOwnershipRelationDO relation = userModelOwnershipRelationService.getOwnerShipRelationByUserAndModel(form.getUserId(), form.getId());

            if (relation == null || relation.getOwnershipType() <= OwnershipTypeEnum.OWN.getCode()) {
                // 所属关系不存在或者已经获得了相同或更高的权限
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "错误的消费类型");
            } else {
                // 可以进行消费，修改金额，修改模型所属关系
                // 获取模型的信息
                ModelDO model = modelService.getModelById(form.getId());
                if (model == null) {
                    throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "消费对象不存在");
                }
                // 获取模型拥有者信息
                UserModelOwnershipRelationDO beforeOwnRelation = userModelOwnershipRelationService.getModelOwnRelation(form.getId());
                if (beforeOwnRelation == null) {
                    throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "消费对象不存在");
                }
                // 转账
                transfer(form.getUserId(), beforeOwnRelation.getUserId(), model.getPurchasePrice());
                // 修改模型所有关系
                userModelOwnershipRelationService.updateModelOwner(form.getUserId(), beforeOwnRelation);
                // NFT所有权转让
                // TODO
                log.info("用户{} 购买了 模型{} 所有权", form.getUserId(), form.getId());
            }
        } else if (consumeType == ConsumeTypeEnum.CONSUME_GRANT_MODEL.getCode()) {
            // 使用模型
            // 获取用户和该对象的所属关系
            UserModelOwnershipRelationDO relation = userModelOwnershipRelationService.getOwnerShipRelationByUserAndModel(form.getUserId(), form.getId());

            if (relation == null || relation.getOwnershipType() <= OwnershipTypeEnum.GRANT.getCode()) {
                // 所属关系不存在或者已经获得了相同或更高的权限
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "错误的消费类型");
            } else {
                // 可以进行消费，修改金额，修改模型所属关系
                // 获取模型的信息
                ModelDO model = modelService.getModelById(form.getId());
                if (model == null) {
                    throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "消费对象不存在");
                }
                // 获取模型拥有者信息
                UserModelOwnershipRelationDO beforeOwnRelation = userModelOwnershipRelationService.getModelOwnRelation(form.getId());
                if (beforeOwnRelation == null) {
                    throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "消费对象不存在");
                }
                // 转账
                transfer(form.getUserId(), beforeOwnRelation.getUserId(), model.getGrantPrice());
                // 添加模型使用权限
                userModelOwnershipRelationService.addUserModelOwnershipRelation(form.getUserId(), model.getId(), OwnershipTypeEnum.GRANT.GRANT);

                // NFT所有权转让
                // TODO
                log.info("用户{} 购买了 模型{} 使用权", form.getUserId(), form.getId());
            }
        } else if (consumeType == ConsumeTypeEnum.CONSUME_EXPERIENCE_MODEL.getCode()) {
            // 体验模型
            // 获取用户和该对象的所属关系
            UserModelOwnershipRelationDO relation = userModelOwnershipRelationService.getOwnerShipRelationByUserAndModel(form.getUserId(), form.getId());

            if (relation == null || relation.getOwnershipType() <= OwnershipTypeEnum.EXPERIENCE.getCode()) {
                // 所属关系不存在或者已经获得了相同或更高的权限
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "错误的消费类型");
            } else {
                // 可以进行消费，修改金额，修改模型所属关系
                // 获取模型的信息
                ModelDO model = modelService.getModelById(form.getId());
                if (model == null) {
                    throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "消费对象不存在");
                }
                // 获取模型拥有者信息
                UserModelOwnershipRelationDO beforeOwnRelation = userModelOwnershipRelationService.getModelOwnRelation(form.getId());
                if (beforeOwnRelation == null) {
                    throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "消费对象不存在");
                }
                // 转账
                transfer(form.getUserId(), beforeOwnRelation.getUserId(), model.getExperiencePrice());
                // 添加模型使用权限
                userModelOwnershipRelationService.addUserModelOwnershipRelation(form.getUserId(), model.getId(), OwnershipTypeEnum.EXPERIENCE);

                // NFT所有权转让
                // TODO
                log.info("用户{} 购买了 模型{} 使用权", form.getUserId(), form.getId());
            }
        } else if (consumeType == ConsumeTypeEnum.CONSUME_PURCHASE_SCENE.getCode()) {
            // TODO
        } else if (consumeType == ConsumeTypeEnum.CONSUME_GRANT_SCENE.getCode()) {
            // TODO
        } else if (consumeType == ConsumeTypeEnum.CONSUME_EXPERIENCE_SCENE.getCode()) {
            // TODO
        } else {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "非法消费类型");
        }
    }

    private void transfer(Long from, Long to, Long amount) {
        UserDO fromUser = userMapper.selectById(from);
        if (fromUser == null) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "用户不存在");
        } else if (fromUser.getBalance() < amount) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "用户余额不足");
        } else {
            fromUser.setBalance(fromUser.getBalance() - amount);
            int affectCount = userMapper.updateById(fromUser);
            if (affectCount <= 0) {
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "转账失败");
            }
        }

        UserDO toUser = userMapper.selectById(to);
        if (toUser == null) {
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "用户不存在");
        } else {
            toUser.setBalance(toUser.getBalance() + amount);
            int affectCount = userMapper.updateById(toUser);
            if (affectCount <= 0) {
                throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "转账失败");
            }
        }
    }
}



