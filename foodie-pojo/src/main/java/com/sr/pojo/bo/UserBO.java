package com.sr.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author SR
 * @date 2019/11/19
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@ApiModel(value = "用户对象BO", description = "客户端传入的数据封装")
public class UserBO {

    @ApiModelProperty(value = "用户名", name="username", example = "test", required = true)
    private String username;

    @ApiModelProperty(value = "密码", name="password", example = "123456", required = true)
    private String password;

    @ApiModelProperty(value = "确认密码", name="confirmPassword", example = "123456", required = false)
    private String confirmPassword;
}
