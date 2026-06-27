package com.my.picturesystembackend.model.dto.outpainting;

import com.my.picturesystembackend.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 扩图任务查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "OutPaintingTaskQueryRequest", description = "AI 扩图任务查询请求")
public class OutPaintingTaskQueryRequest extends PageRequest implements Serializable {

    /**
     * 任务记录 id
     */
    @ApiModelProperty(value = "任务记录 id")
    private Long id;

    /**
     * 阿里云任务 id
     */
    @ApiModelProperty(value = "阿里云任务 id")
    private String taskId;

    /**
     * 原图片 id
     */
    @ApiModelProperty(value = "原图片 id")
    private Long pictureId;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id")
    private Long userId;

    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    private String taskStatus;

    /**
     * 开始创建时间
     */
    @ApiModelProperty(value = "开始创建时间")
    private LocalDateTime startCreateTime;

    /**
     * 结束创建时间
     */
    @ApiModelProperty(value = "结束创建时间")
    private LocalDateTime endCreateTime;

    private static final long serialVersionUID = 1L;
}
