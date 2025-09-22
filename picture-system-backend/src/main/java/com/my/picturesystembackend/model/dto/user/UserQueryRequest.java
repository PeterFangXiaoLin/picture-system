package com.my.picturesystembackend.model.dto.user;

import com.my.picturesystembackend.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询用户请求体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "查询用户请求体")
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id", dataType = "string")
    private Long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String userAccount;

    /**
     * 简介
     */
    @ApiModelProperty(value = "简介")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色：user/admin/ban")
    private String userRole;

    private static final long serialVersionUID = 1L;
}
