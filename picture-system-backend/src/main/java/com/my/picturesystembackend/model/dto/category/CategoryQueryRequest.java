package com.my.picturesystembackend.model.dto.category;

import com.my.picturesystembackend.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 图片分类查询请求体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CategoryQueryRequest", description = "图片分类查询请求体")
public class CategoryQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -3715715828519082872L;

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

    /**
     * 创建用户 id
     */
    @ApiModelProperty(value = "创建用户 id")
    private Long userId;
}
