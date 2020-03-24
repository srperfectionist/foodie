package com.sr.enums;

/**
 * @author shirui
 * @date 2020/2/16
 */
public enum OrderStatusEnum {

    WAIT_PAY(10, "代付款"),
    WAIT_DELIVER(20, "已付款，代发货"),
    WAIT_RECEIVE(30, "已发货，待收货"),
    SUCCESS(40, "交易成功"),
    CLOSE(50, "交易关闭");

    private Integer type;
    private String value;

    OrderStatusEnum(Integer type, String value) {
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
