package com.sr.pojo.vo;

import lombok.*;

/**
 * @author SR
 * @date 2019/12/14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SubCategoryVO {

    private Integer subId;
    private String subName;
    private String subType;
    private String subFatherId;
    
}
