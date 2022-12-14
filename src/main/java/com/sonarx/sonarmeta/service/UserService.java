package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.form.ConsumeActionForm;
import com.sonarx.sonarmeta.domain.form.UpdateUserForm;
import com.sonarx.sonarmeta.domain.model.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_user(用户信息)】的数据库操作Service
* @createDate 2022-08-18 21:40:29
*/
public interface UserService extends IService<UserDO> {

    UserDO getOrCreateUser(String userAddress);

    void consume(ConsumeActionForm form) throws BusinessException;

    UserDO updateUser(UpdateUserForm userForm) throws BusinessException;
}
