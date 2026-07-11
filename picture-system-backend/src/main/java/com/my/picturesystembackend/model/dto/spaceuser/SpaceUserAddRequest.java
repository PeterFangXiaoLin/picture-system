package com.my.picturesystembackend.model.dto.spaceuser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加空间成员请求体
 */
@Data
@ApiModel(value = "SpaceUserAddRequest", description = "添加空间成员请求体")
public class SpaceUserAddRequest implements Serializable {

    /**
     * 空间 id
     */
    @ApiModelProperty(value = "空间 id")
    private Long spaceId;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    @ApiModelProperty(value = "空间角色：viewer/editor/admin")
    private String spaceRole;

    private static final long serialVersionUID = 506785626509959885L;
}
