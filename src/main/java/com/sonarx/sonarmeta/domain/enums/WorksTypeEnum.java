package com.sonarx.sonarmeta.domain.enums;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "作品类别")
public enum WorksTypeEnum {

    WORKS_MODEL(1, "模型"),

    WORKS_SCENE(2, "场景");

    private final Integer code;

    private final String desc;

    WorksTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
