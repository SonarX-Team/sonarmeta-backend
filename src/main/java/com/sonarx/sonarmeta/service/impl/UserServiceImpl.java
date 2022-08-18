package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.model.UserDO;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_user(用户信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
    implements UserService{

    public HttpResult getOrCreateUserProfile(String id) {
    }
}




