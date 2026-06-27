package com.my.picturesystembackend.model.dto.outpainting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 重试 AI 扩图任务请求
 */
@Data
@ApiModel(value = "RetryOutPaintingTaskRequest", description = "重试 AI 扩图任务请求")
public class RetryOutPaintingTaskRequest implements Serializable {

    /**
     * 任务记录 id
     */
    @ApiModelProperty(value = "任务记录 id", required = true)
    private Long id;

    private static final long serialVersionUID = 1L;
}
