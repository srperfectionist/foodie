package com.sr.enums;

/**
 * @author SR
 * @date 2019/11/21
 */
public enum SexEnum {

    WOMAN(0, "女"),
    MAN(1, "男"),
    SECRET(2, "保密");

    private final Integer type;
    private final String value;

    SexEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
