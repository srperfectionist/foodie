package com.sr.pojo;

import lombok.*;

/**
 * @author shirui
 * @date 2020/10/31
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class User {

    private String id;
    private String name;
    private String highLight;
}
