package com.my.picturesystembackend.model.dto.space.analyze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 空间排名分析请求
 */
@Data
@ApiModel(value = "SpaceRankAnalyzeRequest", description = "空间排名分析请求")
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 排名前 N 的空间
     */
    @ApiModelProperty(value = "排名前 N 的空间", example = "10")
    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}
