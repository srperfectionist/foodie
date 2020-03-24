package com.sr.pojo.vo.center;

import lombok.*;

/**
 * @author shirui
 * @date 2020/2/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MySubOrderItemVO {

    private String itemId;
    private String itemImg;
    private String itemName;
    private String itemSpecName;
    private Integer buyCounts;
    private Integer price;
}
