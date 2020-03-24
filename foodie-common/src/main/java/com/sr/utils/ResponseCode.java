package com.sr.utils;

/**
 * @author SR
 * @date 2019/11/21
 *
 */
public enum ResponseCode {

    SUCCESS(200, "SUCCESS"),
    ERROR(500, "ERROR"),
    ERROR_MAP(501, "ERROR_MAP"),
    ERROR_TOKE(502, "ERROR_TOKEN"),
    ERROR_EXCEPTION(555, "ERROR_TOKEN"),
    ILLEAGL_ARGUMENT(2, "ILLEAGL_ARGUMENT"),
    NEED_LOGIN(10, "NEED_LOGIN");

    private final int code;

    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
