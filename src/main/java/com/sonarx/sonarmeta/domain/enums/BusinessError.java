package com.sonarx.sonarmeta.domain.enums;

public enum BusinessError {

    CREATE_MODEL_ERROR("创建模型错误"),

    EDIT_MODEL_ERROR("编辑模型错误"),

    CREATE_SCENE_ERROR("创建场景错误"),

    EDIT_SCENE_ERROR("编辑场景错误"),

    CREATE_USER_AND_MODEL_ERROR("创建用户和模型关系错误"),

    CREATE_USER_AND_SCENE_ERROR("创建用户和场景关系错误"),

    TRANSACTION_ERROR("交易异常");


    private final String desc;

    BusinessError(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
