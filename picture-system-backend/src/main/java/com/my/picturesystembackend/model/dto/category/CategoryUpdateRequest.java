package com.my.picturesystembackend.model.dto.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新分类请求参数
 */
@ApiModel(value = "CategoryUpdateRequest", description = "更新分类请求参数")
@Data
public class CategoryUpdateRequest implements Serializable {

    private static final long serialVersionUID = 7325118332593156175L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String name;
}