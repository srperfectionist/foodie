package com.sr.enums;

/**
 * @author SR
 * @date 2019/12/1
 */
public enum YesOrNoEnum {

    NO(0,"否"),
    YES(1, "是");

    private Integer type;
    private String value;

    YesOrNoEnum(Integer type, String value) {
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
