package com.my.picturesystembackend.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户更新请求
 */
@Data
@ApiModel(value = "UserUpdateRequest", description = "用户更新请求体")
public class UserUpdateRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @NotNull
    private Long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    /**
     * 简介
     */
    @ApiModelProperty(value = "简介")
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @ApiModelProperty(value = "用户角色：user/admin")
    private String userRole;

    private static final long serialVersionUID = 1L;
}
