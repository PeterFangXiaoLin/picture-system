package com.my.picturesystembackend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页请求
 */
@Data
@ApiModel(value = "分页请求")
public class PageRequest {

    /**
     * 当前页号
     */
    @ApiModelProperty(value = "当前页号")
    private int current = 1;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小")
    private int pageSize = 10;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    @ApiModelProperty(value = "排序顺序（默认降序）")
    private String sortOrder = "descend";
}
