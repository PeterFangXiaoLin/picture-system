package com.my.picturesystembackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 扩图任务统计 VO
 */
@Data
@ApiModel(value = "OutPaintingTaskStatisticsVO", description = "AI 扩图任务统计 VO")
public class OutPaintingTaskStatisticsVO implements Serializable {

    @ApiModelProperty(value = "任务总数")
    private Long totalCount;

    @ApiModelProperty(value = "成功任务数")
    private Long successCount;

    @ApiModelProperty(value = "失败任务数")
    private Long failedCount;

    @ApiModelProperty(value = "处理中任务数")
    private Long processingCount;

    @ApiModelProperty(value = "成功率（百分比，保留两位小数）")
    private Double successRate;

    @ApiModelProperty(value = "失败率（百分比，保留两位小数）")
    private Double failureRate;

    private static final long serialVersionUID = 1L;
}
