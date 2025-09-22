package com.my.picturesystembackend.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户添加请求体
 */
@Data
@ApiModel(value = "UserAddRequest", description = "用户添加请求体")
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    @Size(min = 4, message = "账号长度不小于4位")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    /**
     * 用户简介
     */
    @ApiModelProperty(value = "用户简介")
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    @ApiModelProperty(value = "用户角色: user, admin")
    private String userRole;

    private static final long serialVersionUID = 1L;
}
