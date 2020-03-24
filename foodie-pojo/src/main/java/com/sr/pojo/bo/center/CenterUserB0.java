package com.sr.pojo.bo.center;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @author shirui
 * @date 2020/2/23
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@ApiModel(value = "用户对象", description = "从客户端，由用户传入的数据封装在此entity")
public class CenterUserB0 {

    @ApiModelProperty(value = "用户名", name = "username", example = "json", required = false)
    private String username;

    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = false)
    private String password;

    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "123456", required = false)
    private String confirmPassword;

    @NotBlank(message = "用户昵称不能为空")
    @Length(max = 12, message = "用户昵称不能超过12位")
    @ApiModelProperty(value = "用户昵称", name = "nickname", example = "nickname", required = false)
    private String nickname;

    @Length(max = 12, message = "用户真实姓名不能超过12位")
    @ApiModelProperty(value = "真实姓名", name = "realname", example = "realname", required = false)
    private String realname;

    @Pattern(regexp = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号", name = "mobile", example = "mobile", required = false)
    private String mobile;

    @Email
    @ApiModelProperty(value = "邮箱地址", name = "email", example = "email@email.com", required = false)
    private String email;

    @Min(value = 0, message = "性别选择不正确")
    @Max(value = 2, message = "性别选择不正确")
    @ApiModelProperty(value = "性别", name = "sex", example = "0：女 1：男 2：保密", required = false)
    private Integer sex;

    @ApiModelProperty(value = "生日", name = "birthday", example = "1900-01-01", required = false)
    private Date birthday;
}
