package com.my.picturesystembackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 脱敏后的用户
 */
@Data
@ApiModel(value = "UserVO", description = "脱敏后的用户")
public class UserVO implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;
    
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String userAccount;

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
     * 用户简介
     */
    @ApiModelProperty(value = "用户简介")
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @ApiModelProperty(value = "用户角色：user/admin")
    private String userRole;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}
