package com.sonarx.sonarmeta.domain.enums;

public enum BusinessError {

    /**
     * 用户错误
     */

    USER_NOT_EXIST_ERROR("用户不存在"),

    USER_SCENE_PERMISSION_DENIED("用户没有场景权限"),

    USER_MODEL_PERMISSION_DENIED("用户没有模型权限"),


    /**
     * 模型/场景错误
     */

    MODEL_NOT_EXIST("模型不存在"),

    SCENE_NOT_EXIST("场景不存在"),

    CREATE_MODEL_ERROR("创建模型错误"),

    EDIT_MODEL_ERROR("编辑模型错误"),

    CREATE_SCENE_ERROR("创建场景错误"),

    EDIT_SCENE_ERROR("编辑场景错误"),

    CREATE_USER_AND_MODEL_ERROR("创建用户和模型关系错误"),

    CREATE_USER_AND_SCENE_ERROR("创建用户和场景关系错误"),


    /**
     * 交易错误
     */
    INCORRECT_CONTRACT("合约异常"),

    ETH_TRANSACTION_ERROR("交易失败"),

    OUT_OF_BALANCE_ERROR("余额不足"),

    TRANSACTION_TYPE_ERROR("消费类型错误"),

    TRANSACTION_OBJECT_NOT_EXIST("消费对象不存在"),

    TRANSACTION_ERROR("转账异常");




    private final String desc;

    BusinessError(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
