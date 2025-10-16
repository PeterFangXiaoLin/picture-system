package com.my.picturesystembackend.model.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新标签请求体
 */
@Data
@ApiModel(value = "TagUpdateRequest", description = "更新标签请求体")
public class TagUpdateRequest implements Serializable {

    private static final long serialVersionUID = -3211474446256490142L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    private String name;
}
