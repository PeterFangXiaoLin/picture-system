package com.my.picturesystembackend.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片批量编辑请求类
 */
@Data
@ApiModel(value = "PictureEditByBatchRequest", description = "图片批量编辑请求类")
public class PictureEditByBatchRequest implements Serializable {

    /**
     * 图片 id 列表
     */
    @ApiModelProperty("图片id列表")
    private List<Long> pictureIdList;

    /**
     * 空间 id
     */
    @ApiModelProperty("空间id")
    private Long spaceId;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private Long categoryId;

    /**
     * 标签
     */
    @ApiModelProperty("标签")
    private List<Long> tags;

    /**
     * 命名规则
     */
    @ApiModelProperty("命名规则")
    private String nameRule;

    private static final long serialVersionUID = 1L;
}
