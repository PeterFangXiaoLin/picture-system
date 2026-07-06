package com.my.picturesystembackend.model.vo.space.analyze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 空间标签分析响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SpaceTagAnalyzeResponse", description = "空间标签分析响应")
public class SpaceTagAnalyzeResponse implements Serializable {

    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id")
    private Long tagId;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    private String tagName;

    /**
     * 使用次数
     */
    @ApiModelProperty(value = "使用次数")
    private Long count;

    private static final long serialVersionUID = 1L;
}
