package com.sr.enums;

/**
 * @author shirui
 * @date 2020/2/13
 */
public enum PayMethodEnum {

    WEIXIN(1, "微信"),
    ALIPAY(2, "支付宝");

    private Integer type;
    private String  code;

    PayMethodEnum(Integer type, String code) {
        this.type = type;
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
