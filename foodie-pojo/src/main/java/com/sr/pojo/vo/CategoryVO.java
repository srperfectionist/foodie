package com.sr.pojo.vo;

import lombok.*;

import java.util.List;

/**
 * @author SR
 * @date 2019/12/14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CategoryVO {

    private Integer id;
    private String name;
    private String type;
    private String fatherId;
    private List<SubCategoryVO> subCatList;
}
