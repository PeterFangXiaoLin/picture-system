package com.my.picturesystembackend.model.vo.space.analyze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SpaceSizeAnalyzeResponse", description = "空间大小分析响应")
public class SpaceSizeAnalyzeResponse implements Serializable {

    /**
     * 图片大小范围
     */
    @ApiModelProperty(value = "图片大小范围")
    private String sizeRange;

    /**
     * 图片数量
     */
    @ApiModelProperty(value = "图片数量")
    private Long count;

    private static final long serialVersionUID = 1L;
}
