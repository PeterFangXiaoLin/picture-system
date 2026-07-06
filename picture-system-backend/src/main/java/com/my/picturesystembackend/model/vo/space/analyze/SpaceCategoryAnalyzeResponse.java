package com.my.picturesystembackend.model.vo.space.analyze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 空间图片分类响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SpaceCategoryAnalyzeResponse", description = "空间图片分类响应体")
public class SpaceCategoryAnalyzeResponse implements Serializable {

    /**
     * 图片分类
     */
    @ApiModelProperty(value = "图片分类")
    private Long categoryId;

    /**
     * 图片分类名称
     */
    @ApiModelProperty(value = "图片分类名称")
    private String categoryName;

    /**
     * 图片数量
     */
    @ApiModelProperty(value = "图片数量")
    private Long count;

    /**
     * 分类图片总大小
     */
    @ApiModelProperty(value = "分类图片总大小")
    private Long totalSize;

    private static final long serialVersionUID = 1L;
}
