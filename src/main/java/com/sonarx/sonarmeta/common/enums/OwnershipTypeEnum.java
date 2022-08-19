package com.sonarx.sonarmeta.common.enums;

public enum OwnershipTypeEnum {

    OWN(1, "拥有权"),

    EXPERIENCE(2, "体验权"),

    GRANT(3, "使用权");

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
