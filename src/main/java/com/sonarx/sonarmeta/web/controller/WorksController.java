package com.sonarx.sonarmeta.web.controller;

import com.sonarx.sonarmeta.domain.common.HttpResult;
import com.sonarx.sonarmeta.domain.common.PageParam;
import com.sonarx.sonarmeta.domain.common.PageWrapper;
import com.sonarx.sonarmeta.domain.query.AllWorksListQuery;
import com.sonarx.sonarmeta.domain.query.SearchWorksListQuery;
import com.sonarx.sonarmeta.domain.view.WorksView;
import com.sonarx.sonarmeta.service.WorksService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.sonarx.sonarmeta.common.Constants.API_PREFIX;

/**
 * @Description: Controller for works.
 * @author: liuxuanming
 */
@Api("作品信息接口")
@Slf4j
@RestController
@RequestMapping(API_PREFIX + "/works")
public class WorksController {

    @Resource
    WorksService worksService;

    @ApiOperation(value = "作品列表")
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    public HttpResult<PageWrapper<WorksView>> getWorksList(@ModelAttribute AllWorksListQuery query) {
        return HttpResult.successResult(worksService.getWorksList(query));
    }

    @ApiOperation(value = "搜索作品")
    @RequestMapping(value = "/search", method = {RequestMethod.GET})
    public HttpResult<PageWrapper<WorksView>> searchWorks(@ModelAttribute SearchWorksListQuery query) {
        return HttpResult.successResult(worksService.searchWorksList(query));
    }
}
