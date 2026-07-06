package com.my.picturesystembackend.model.vo.space.analyze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 空间资源使用分析响应类
 */
@Data
@ApiModel(value = "SpaceUsageAnalyzeResponse", description = "空间资源使用分析响应类")
public class SpaceUsageAnalyzeResponse implements Serializable {

    /**
     * 已使用大小
     */
    @ApiModelProperty(value = "已使用大小")
    private Long usedSize;

    /**
     * 总大小
     */
    @ApiModelProperty(value = "总大小")
    private Long maxSize;

    /**
     * 空间使用比例
     */
    @ApiModelProperty(value = "空间使用比例")
    private Double sizeUsageRatio;

    /**
     * 当前图片数量
     */
    @ApiModelProperty(value = "当前图片数量")
    private Long usedCount;

    /**
     * 最大图片数量
     */
    @ApiModelProperty(value = "最大图片数量")
    private Long maxCount;

    /**
     * 图片数量占比
     */
    @ApiModelProperty(value = "图片数量占比")
    private Double countUsageRatio;

    private static final long serialVersionUID = 1L;
}
