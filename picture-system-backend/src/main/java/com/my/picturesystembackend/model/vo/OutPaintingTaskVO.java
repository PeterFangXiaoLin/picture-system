package com.my.picturesystembackend.model.vo;

import com.my.picturesystembackend.api.aliyunai.model.CreateOutPaintingTaskRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 扩图任务 VO
 */
@Data
@ApiModel(value = "OutPaintingTaskVO", description = "AI 扩图任务 VO")
public class OutPaintingTaskVO implements Serializable {

    @ApiModelProperty(value = "任务记录 id")
    private Long id;

    @ApiModelProperty(value = "阿里云任务 id")
    private String taskId;

    @ApiModelProperty(value = "原图片 id")
    private Long pictureId;

    @ApiModelProperty(value = "原图片名称")
    private String pictureName;

    @ApiModelProperty(value = "创建用户 id")
    private Long userId;

    @ApiModelProperty(value = "任务状态")
    private String taskStatus;

    @ApiModelProperty(value = "扩图参数")
    private CreateOutPaintingTaskRequest.Parameters parameters;

    @ApiModelProperty(value = "原图 url")
    private String originalImageUrl;

    @ApiModelProperty(value = "输出图像 url")
    private String outputImageUrl;

    @ApiModelProperty(value = "错误码")
    private String errorCode;

    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

    @ApiModelProperty(value = "重试时关联的原任务记录 id")
    private Long parentTaskId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}
