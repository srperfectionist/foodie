package com.sr.pojo.vo;

import lombok.*;

import java.util.Date;

/**
 * @author shirui
 * @date 2020/2/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MyCommentVO {

    private String commentId;
    private String content;
    private Date createdTime;
    private String itemId;
    private String itemName;
    private String specName;
    private String itemImg;
}
