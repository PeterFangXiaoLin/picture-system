package com.my.picturesystembackend.model.dto.spaceuser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询空间成员请求体
 */
@Data
@ApiModel(value = "SpaceUserQueryRequest", description = "查询空间成员请求体")
public class SpaceUserQueryRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

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

    /**
     * 邀请确认状态：0-待确认；1-已接受；2-已拒绝
     */
    @ApiModelProperty(value = "邀请确认状态：0-待确认；1-已接受；2-已拒绝")
    private Integer inviteStatus;

    /**
     * 邀请人 id
     */
    @ApiModelProperty(value = "邀请人 id")
    private Long createUserId;

    private static final long serialVersionUID = 9015929366455466611L;
}
