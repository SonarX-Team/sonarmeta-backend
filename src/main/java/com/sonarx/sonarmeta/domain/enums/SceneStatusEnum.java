package com.sonarx.sonarmeta.domain.enums;

import io.swagger.annotations.ApiModel;

/**
 * @description: 场景状态枚举
 * @author: liuxuanming
 */
@ApiModel(description = "场景状态类别")
public enum SceneStatusEnum {

    SCENE_STATUS_UNRELEASED(1, "未发布"),

    SCENE_STATUS_CHECKING(2, "审核中"),

    SCENE_STATUS_PASSED(3, "已发布"),

    SCENE_STATUS_CHECK_FAILED(3, "审核不通过");

    private final Integer statusCode;

    private final String desc;

    SceneStatusEnum(int code, String desc) {
        this.statusCode = code;
        this.desc = desc;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getDesc() {
        return desc;
    }
}
