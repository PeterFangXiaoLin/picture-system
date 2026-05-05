package com.my.picturesystembackend.model.dto.space;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "SpaceLevel", description = "空间级别")
public class SpaceLevel {

    /**
     * 值
     */
    @ApiModelProperty("值")
    private int value;

    /**
     * 中文
     */
    @ApiModelProperty("文本")
    private String text;

    /**
     * 最大数量
     */
    @ApiModelProperty("空间最大数量")
    private long maxCount;

    /**
     * 最大大小
     */
    @ApiModelProperty("空间最大大小")
    private long maxSize;
}
