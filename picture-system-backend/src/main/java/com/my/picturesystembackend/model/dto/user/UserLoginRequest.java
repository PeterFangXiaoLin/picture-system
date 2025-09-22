package com.my.picturesystembackend.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户登录请求体
 */
@Data
@ApiModel(value = "用户登录请求体")
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 6621180416587383582L;

    /**
     * 账号
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 4, message = "用户账号长度不能小于4")
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "用户密码长度需在6-20之间")
    @ApiModelProperty(value = "用户密码")
    private String userPassword;
}
