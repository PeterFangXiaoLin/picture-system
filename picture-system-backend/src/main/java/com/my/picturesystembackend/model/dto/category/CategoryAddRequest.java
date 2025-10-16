package com.my.picturesystembackend.model.dto.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 分类添加请求体
 */
@Data
@ApiModel(value = "CategoryAddRequest", description = "分类添加请求体")
public class CategoryAddRequest implements Serializable {

    private static final long serialVersionUID = -7620299801744118625L;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    @NotBlank
    private String name;
}
