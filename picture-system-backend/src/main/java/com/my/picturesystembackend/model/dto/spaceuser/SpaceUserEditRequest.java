package com.my.picturesystembackend.model.dto.spaceuser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 编辑空间成员请求体
 */
@Data
@ApiModel(value = "SpaceUserEditRequest", description = "编辑空间成员请求体")
public class SpaceUserEditRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    @ApiModelProperty(value = "空间角色：viewer/editor/admin")
    private String spaceRole;

    private static final long serialVersionUID = -54004844571018171L;
}
