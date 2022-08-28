package com.sonarx.sonarmeta.domain.enums;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "所有权类别")
public enum OwnershipTypeEnum {

    MODEL_CREATOR(1, "模型创建者"),

    MODEL_OWNER(2, "模型拥有者"),

    MODEL_GRANTOR(3, "模型授权者"),

    SCENE_CREATOR(4, "场景创建者"),

    SCENE_OWNER(5, "场景拥有者"),

    SCENE_DIVER(6, "场景体验者");

    private final Integer code;

    private final String desc;

    OwnershipTypeEnum(int code, String desc) {
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
