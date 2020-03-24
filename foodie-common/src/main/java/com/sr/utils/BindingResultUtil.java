package com.sr.utils;

import com.google.common.collect.Maps;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

/**
 * @author shirui
 * @date 2020/2/23
 */
public class BindingResultUtil {

    public static Map<String, String> getErrors(BindingResult result){
        Map<String, String> maps = Maps.newHashMap();
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            // 发生验证错误所有对应的某一个属性
            String field = fieldError.getField();
            // 验证错误的信息
            String errorMsg = fieldError.getDefaultMessage();

            maps.put(field, errorMsg);
        }

        return maps;
    }
}
