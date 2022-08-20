package com.sonarx.sonarmeta.domain.enums;

import io.swagger.annotations.ApiModel;

/**
 * @description: 模型状态枚举
 * @author: liuxuanming
 */
@ApiModel(description = "模型状态类别")
public enum ModelStatusEnum {

    MODEL_STATUS_UNRELEASED(1, "未发布"),

    MODEL_STATUS_CHECKING(2, "审核中"),

    MODEL_STATUS_PASSED(3, "已发布"),

    MODEL_STATUS_CHECK_FAILED(4, "审核不通过");

    private final Integer statusCode;

    private final String desc;

    ModelStatusEnum(int code, String desc) {
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
