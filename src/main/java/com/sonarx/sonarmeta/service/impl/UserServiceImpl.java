package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.enums.BusinessError;
import com.sonarx.sonarmeta.domain.enums.ConsumeTypeEnum;
import com.sonarx.sonarmeta.domain.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.enums.OwnershipTypeEnum;
import com.sonarx.sonarmeta.domain.form.ConsumeActionForm;
import com.sonarx.sonarmeta.domain.form.UpdateUserForm;
import com.sonarx.sonarmeta.domain.model.*;
import com.sonarx.sonarmeta.mapper.UserMapper;
import com.sonarx.sonarmeta.service.ModelService;
import com.sonarx.sonarmeta.service.SceneService;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.service.Web3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

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
    ModelService modelService;

    @Resource
    SceneService sceneService;

    @Resource
    Web3Service web3Service;

    @Override
    public UserDO getOrCreateUser(String address) {
        UserDO user = userMapper.selectById(address);
        if (user == null) {
            UserDO newUser = new UserDO();
            newUser.setAddress(address);
            newUser.setUsername(APP_NAME + DEFAULT_CONNECTOR + address);
            userMapper.insert(newUser);
            return newUser;
        }
        return user;
    }

    /**
     * 根据不同的消费类型处理
     *
     * @param form
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consume(ConsumeActionForm form) throws BusinessException {
        Integer consumeType = form.getType();
        if (consumeType.equals(ConsumeTypeEnum.CONSUME_PURCHASE_MODEL.getCode())) {
            UserDO modelCreator = modelService.getModelOwnerOrCreator(form.getId(), OwnershipTypeEnum.MODEL_CREATOR.getCode());
            UserDO modelOwner = modelService.getModelOwnerOrCreator(form.getId(), OwnershipTypeEnum.MODEL_OWNER.getCode());
            // 购买模型
            // 获取用户和该对象的所属关系
            if (form.getUserAddress().equals(modelCreator.getAddress()) || form.getUserAddress().equals(modelOwner.getAddress())) {
                // 创建者、拥有者不能买
                throw new BusinessException(BusinessError.TRANSACTION_TYPE_ERROR);
            } else {
                // 可以进行消费，修改金额，修改模型所属关系
                // 获取模型的信息
                ModelDO model = modelService.getById(form.getId());
                if (model == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 获取模型拥有者信息
                UserModelOwnershipRelationDO beforeOwnRelation = modelService.getModelOwnRelation(form.getId());
                if (beforeOwnRelation == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 修改模型所有关系
                modelService.updateModelOwner(form.getUserAddress(), beforeOwnRelation);
                // 转账
                transfer(form.getUserAddress(), beforeOwnRelation.getAddress(), model.getTokenPrice());
                // 钱包代理转账：从该买家转向模型拥有者
                web3Service.transferERC20UsingSonarMetaAllowance(form.getUserAddress(), beforeOwnRelation.getAddress(), model.getTokenPrice());
                // NFT所有权转让
                web3Service.transferERC721UsingSonarMetaApproval(model.getNftTokenId(), form.getUserAddress());
                log.info("用户{} 购买了 模型{} 所有权", form.getUserAddress(), form.getId());
                // TODO 另开线程/消息队列处理分红，调用erc20的transfer方法
            }
        } else if (consumeType.equals(ConsumeTypeEnum.CONSUME_GRANT_MODEL.getCode())) {
            UserDO modelCreator = modelService.getModelOwnerOrCreator(form.getId(), OwnershipTypeEnum.MODEL_CREATOR.getCode());
            UserDO modelOwner = modelService.getModelOwnerOrCreator(form.getId(), OwnershipTypeEnum.MODEL_OWNER.getCode());
            List<UserDO> modelGrantors = modelService.getModelGrantors(form.getId());
            // 使用模型
            // 获取用户和该对象的所属关系
            if (form.getUserAddress().equals(modelCreator.getAddress()) ||
                    form.getUserAddress().equals(modelOwner.getAddress()) ||
                    (modelGrantors != null && modelGrantors.stream().anyMatch(modelGrantor -> form.getUserAddress().equals(modelGrantor.getAddress())))
            ) {
                // 创建者、拥有者、授权者不能获得授权
                throw new BusinessException(BusinessError.TRANSACTION_TYPE_ERROR);
            } else {
                // 可以进行消费，修改金额，修改模型所属关系
                // 获取模型的信息
                ModelDO model = modelService.getById(form.getId());
                if (model == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 获取模型拥有者信息
                UserModelOwnershipRelationDO beforeOwnRelation = modelService.getModelOwnRelation(form.getId());
                if (beforeOwnRelation == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 添加模型使用权限
                modelService.addUserModelOwnershipRelation(form.getUserAddress(), model.getId(), OwnershipTypeEnum.MODEL_GRANTOR);
                // 转账
                transfer(form.getUserAddress(), beforeOwnRelation.getAddress(), model.getGrantPrice());
                // 钱包代理转账：从该买家转向模型拥有者
                web3Service.transferERC20UsingSonarMetaAllowance(form.getUserAddress(), beforeOwnRelation.getAddress(), model.getGrantPrice());
                // NFT所有权转让
                web3Service.grantERC721UsingSonarMetaApproval(model.getNftTokenId(), form.getUserAddress());
                log.info("用户{} 购买了 模型{} 使用权", form.getUserAddress(), form.getId());
                // TODO 另开线程/消息队列处理分红，调用erc20的transfer方法
            }
        } else if (consumeType.equals(ConsumeTypeEnum.CONSUME_PURCHASE_SCENE.getCode())) {
            UserDO sceneCreator = sceneService.getSceneOwnerOrCreator(form.getId(), OwnershipTypeEnum.SCENE_CREATOR.getCode());
            UserDO sceneOwner = sceneService.getSceneOwnerOrCreator(form.getId(), OwnershipTypeEnum.SCENE_OWNER.getCode());
            // 购买场景
            // 获取用户和该对象的所属关系
            if (form.getUserAddress().equals(sceneCreator.getAddress()) || form.getUserAddress().equals(sceneOwner.getAddress())) {
                // 创建者、拥有者不能买
                throw new BusinessException(BusinessError.TRANSACTION_TYPE_ERROR);
            } else {
                // 可以进行消费，修改金额，修改场景所属关系
                // 获取场景的信息
                SceneDO scene = sceneService.getById(form.getId());
                if (scene == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 获取场景拥有者信息
                UserSceneOwnershipRelationDO beforeOwnRelation = sceneService.getSceneOwnRelation(form.getId());
                if (beforeOwnRelation == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 修改场景所有关系
                sceneService.updateSceneOwner(form.getUserAddress(), beforeOwnRelation);
                // 转账
                transfer(form.getUserAddress(), beforeOwnRelation.getAddress(), scene.getTokenPrice());
                // 钱包代理转账：从该买家转向场景拥有者
                web3Service.transferERC20UsingSonarMetaAllowance(form.getUserAddress(), beforeOwnRelation.getAddress(), scene.getTokenPrice());
                // NFT所有权转让
                web3Service.transferERC998UsingSonarMetaApproval(scene.getNftTokenId(), form.getUserAddress());
                log.info("用户{} 购买了 场景{} 所有权", form.getUserAddress(), form.getId());
                // TODO 另开线程/消息队列处理分红，调用erc20的transfer方法
            }
        } else if (consumeType.equals(ConsumeTypeEnum.CONSUME_DIVE_SCENE.getCode())) {
            UserDO sceneCreator = sceneService.getSceneOwnerOrCreator(form.getId(), OwnershipTypeEnum.SCENE_CREATOR.getCode());
            UserDO sceneOwner = sceneService.getSceneOwnerOrCreator(form.getId(), OwnershipTypeEnum.SCENE_OWNER.getCode());
            List<UserDO> sceneDivers = sceneService.getSceneDivers(form.getId());
            // 体验场景
            // 获取用户和该对象的所属关系
            if (form.getUserAddress().equals(sceneCreator.getAddress()) ||
                    form.getUserAddress().equals(sceneOwner.getAddress()) ||
                    (sceneDivers != null && sceneDivers.stream().anyMatch(sceneDiver -> form.getUserAddress().equals(sceneDiver.getAddress())))
            ) {
                // 创建者、拥有者、体验者不能获得授权
                throw new BusinessException(BusinessError.TRANSACTION_TYPE_ERROR);
            } else {
                // 可以进行消费，修改金额，修改场景所属关系
                // 获取场景的信息
                SceneDO scene = sceneService.getById(form.getId());
                if (scene == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 获取场景拥有者信息
                UserSceneOwnershipRelationDO beforeOwnRelation = sceneService.getSceneOwnRelation(form.getId());
                if (beforeOwnRelation == null) {
                    throw new BusinessException(BusinessError.TRANSACTION_OBJECT_NOT_EXIST);
                }
                // 添加场景体验权限
                sceneService.addUserSceneOwnershipRelation(form.getUserAddress(), scene.getId(), OwnershipTypeEnum.SCENE_DIVER);
                // 转账
                transfer(form.getUserAddress(), beforeOwnRelation.getAddress(), scene.getDivePrice());
                // 钱包代理转账：从该买家转向场景拥有者
                web3Service.transferERC20UsingSonarMetaAllowance(form.getUserAddress(), beforeOwnRelation.getAddress(), scene.getDivePrice());
                log.info("用户{} 购买了 场景{} 体验权", form.getUserAddress(), form.getId());
                // TODO 另开线程/消息队列处理分红，调用erc20的transfer方法
            }
        } else {
            throw new BusinessException(BusinessError.TRANSACTION_TYPE_ERROR);
        }
    }

    @Override
    public UserDO updateUser(UpdateUserForm form) throws BusinessException {
        UserDO user = userMapper.selectById(form.getUserAddress());
        if (user == null) {
            throw new BusinessException(BusinessError.USER_NOT_EXIST_ERROR);
        }
        user.setUsername(form.getUsername());
        user.setTelephone(form.getTelephone());
        user.setEmail(form.getEmail());
        user.setAvatar(form.getAvatar());
        user.setGender(form.getGender());
        user.setDescription(form.getDescription());
        user.setBirthDate(form.getBirthDate());
        user.setWechat(form.getWechat());
        user.setTwitter(form.getTwitter());
        user.setBalance(form.getBalance());
        int affectCount = userMapper.updateById(user);
        if (affectCount > 0) {
            log.info("插入用户信息成功，钱包地址：{}", user.getAddress());
            user = userMapper.selectById(user.getAddress());
            log.info("获取用户钱包地址：{}", user.getAddress());
        } else {
            log.warn("插入用户信息失败，钱包地址：{}", user.getAddress());
            throw new BusinessException(ErrorCodeEnum.FAIL.getCode(), "插入用户信息失败");
        }
        return user;
    }

    private void transfer(String from, String to, Double amount) throws BusinessException {
        UserDO fromUser = userMapper.selectById(from);
        if (fromUser == null) {
            throw new BusinessException(BusinessError.USER_NOT_EXIST_ERROR);
        } else if (fromUser.getBalance() < amount) {
            throw new BusinessException(BusinessError.OUT_OF_BALANCE_ERROR);
        } else {
            fromUser.setBalance(fromUser.getBalance() - amount);
            int affectCount = userMapper.updateById(fromUser);
            if (affectCount <= 0) {
                throw new BusinessException(BusinessError.TRANSACTION_ERROR);
            }
        }

        UserDO toUser = userMapper.selectById(to);
        if (toUser == null) {
            throw new BusinessException(BusinessError.USER_NOT_EXIST_ERROR);
        } else {
            toUser.setBalance(toUser.getBalance() + amount);
            int affectCount = userMapper.updateById(toUser);
            if (affectCount <= 0) {
                throw new BusinessException(BusinessError.TRANSACTION_ERROR);
            }
        }
    }
}




