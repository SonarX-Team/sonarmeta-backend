package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.sonarx.sonarmeta.common.Constants.API_PREFIX;

/**
 * @Description: Controller for user actions.
 * @author: liuxuanming
 */
@Api("用户信息接口")
@Slf4j
@RestController
@RequestMapping(API_PREFIX + "/user")
public class UserController {

//    @Resource
//    private UserServiceImpl userService;
//
//    @ApiOperation(value = "获取用户个人信息")
//    @RequestMapping(value = "/profile", method = {RequestMethod.GET})
//    public HttpResult getUserProfile(@RequestParam(value = "id") String id) {
//        return  userService.getOrCreateUserProfile(id);
//    }

    @ApiOperation(value = "获取用户个人空间作品列表")
    @RequestMapping(value = "/workslist", method = {RequestMethod.GET})
    public HttpResult getUserWorksList() {
            return  HttpResult.successResult();
    }

    @ApiOperation(value = "用户行为")
    @RequestMapping(value = "/action", method = {RequestMethod.POST})
    public HttpResult userActions() {
        return  HttpResult.successResult();
    }

}
