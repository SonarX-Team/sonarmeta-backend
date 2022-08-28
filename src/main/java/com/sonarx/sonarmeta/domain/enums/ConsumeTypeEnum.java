package com.sonarx.sonarmeta.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;

@ApiModel(description = "消费类型")
public enum ConsumeTypeEnum {

    CONSUME_PURCHASE_MODEL(1, "购买模型"),

    CONSUME_GRANT_MODEL(2, "使用模型"),

    CONSUME_PURCHASE_SCENE(3, "购买场景"),

    CONSUME_DIVE_SCENE(4, "体验场景");

    private final Integer code;

    private final String desc;

    ConsumeTypeEnum(int code, String desc) {
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
