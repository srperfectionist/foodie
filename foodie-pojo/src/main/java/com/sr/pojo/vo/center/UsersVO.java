package com.sr.pojo.vo.center;

import lombok.*;

/**
 * @author shirui
 * @date 2020/2/23
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UsersVO {

    private String id;

    private String username;

    private String nickname;

    private String realname;

    private String face;

    private Integer sex;
}
