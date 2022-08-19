package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.common.enums.ErrorCodeEnum;
import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.model.UserDO;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private UserMapper userMapper;

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
        UserDO user = userMapper.selectById(id);
        return user;
    }
}




