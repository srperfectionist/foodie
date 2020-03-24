package com.sr.pojo.vo;

import lombok.*;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/16
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NewItemsVO {

    private Integer rootCatId;
    private String rootCatName;
    private String slogan;
    private String catImage;
    private String bgColor;

    private List<SimpleItemVO> simpleItemList;
}
