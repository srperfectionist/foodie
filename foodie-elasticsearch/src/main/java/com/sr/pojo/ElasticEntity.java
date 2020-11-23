package com.sr.pojo;

import lombok.*;

import java.util.Map;

/**
 * @author shirui
 * @date 2020/10/23
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ElasticEntity {

    /**
     * 主键标识
     */
    private String id;

    /**
     * 数据存储
     */
    private Map data;
}
