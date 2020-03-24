package com.sr.pojo.vo;

import lombok.*;

/**
 * @author SR
 * @date 2020/1/8
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SearchItemsVO {

    private String itemId;
    private String itemName;
    private Integer sellCounts;
    private String imgUrl;
    private Integer price;
}
