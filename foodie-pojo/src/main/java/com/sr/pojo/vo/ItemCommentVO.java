package com.sr.pojo.vo;

import lombok.*;

import java.util.Date;

/**
 * @author SR
 * @date 2019/12/26
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ItemCommentVO {

    private Integer commentLevel;
    private String content;
    private String specName;
    private Date createdTime;
    private String userFace;
    private String nickname;
}
