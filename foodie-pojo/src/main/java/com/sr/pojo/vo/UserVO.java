package com.sr.pojo.vo;

import lombok.*;

/**
 * @author SR
 * @date 2019/11/21
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserVO {

    private String id;

    private String username;

    private String nickname;

    private String realname;

    private String face;

    private Integer sex;

    private String userUniqueToken;
}
