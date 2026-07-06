package com.my.picturesystembackend.model.dto.space.analyze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "SpaceAnalyzeRequest", description = "空间分析请求")
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间 ID
     */
    @ApiModelProperty(value = "空间 ID")
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    @ApiModelProperty(value = "是否查询公共图库")
    private boolean queryPublic;

    /**
     * 全空间分析
     */
    @ApiModelProperty(value = "全空间分析")
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}
