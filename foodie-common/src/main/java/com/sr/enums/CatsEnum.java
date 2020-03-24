package com.sr.enums;

/**
 * @author SR
 * @date 2019/12/1
 */
public enum CatsEnum {

    ONE(1, "一级分类");

    private Integer type;
    private String value;

    CatsEnum(Integer type, String value) {
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
