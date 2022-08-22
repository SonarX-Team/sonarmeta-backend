package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.form.ConsumeActionForm;
import com.sonarx.sonarmeta.domain.form.UpdateUserForm;
import com.sonarx.sonarmeta.domain.model.UserDO;
import com.sonarx.sonarmeta.domain.view.WorksView;
import com.sonarx.sonarmeta.service.UserService;
import com.sonarx.sonarmeta.service.WorksService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

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

    @Resource
    UserService userService;

    @Resource
    WorksService worksService;

    @ApiOperation(value = "通过钱包地址获取用户个人信息")
    @RequestMapping(value = "/profile/byAddress", method = {RequestMethod.GET})
    public HttpResult<UserDO> getUserProfileByAddress(@RequestParam(value = "address") String address) {
        return HttpResult.successResult(userService.getOrCreateUserProfileByAddress(address));
    }

    @ApiOperation(value = "通过ID获取用户个人信息")
    @RequestMapping(value = "/profile/byId", method = {RequestMethod.GET})
    public HttpResult<UserDO> getUserProfileById(@RequestParam(value = "id") String id) {
        UserDO user = userService.getUserProfileById(id);
        if (user == null) {
            return HttpResult.errorResult("用户不存在");
        } else {
            return HttpResult.successResult(user);
        }
    }


    @ApiOperation(value = "获取用户个人空间作品列表")
    @RequestMapping(value = "/workslist", method = {RequestMethod.GET})
    public HttpResult<Map<Integer, List<WorksView>>> getUserWorksList(@RequestParam(value = "id") String id) {
        UserDO user = userService.getUserProfileById(id);
        if (user == null) {
            return HttpResult.errorResult("用户不存在");
        } else {
            return HttpResult.successResult(worksService.getWorksByUserId(id));
        }
    }

    @ApiOperation(value = "用户消费行为")
    @RequestMapping(value = "/consume", method = {RequestMethod.POST})
    public HttpResult userActions(@RequestBody @Validated ConsumeActionForm form) {
        userService.consume(form);
        return HttpResult.successResult();
    }

    @ApiOperation(value = "更新用户个人信息")
    @RequestMapping(value = "/profile/update", method = {RequestMethod.POST})
    public HttpResult updateUserProfile(@RequestBody @Validated UpdateUserForm userForm) {
        userService.updateUser(userForm);
        return HttpResult.successResult();
    }
}
