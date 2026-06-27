package com.my.picturesystembackend.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 扩图任务记录
 * @TableName out_painting_task
 */
@TableName(value = "out_painting_task")
@Data
public class OutPaintingTask implements Serializable {

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 阿里云任务 id
     */
    @TableField(value = "taskId")
    private String taskId;

    /**
     * 原图片 id
     */
    @TableField(value = "pictureId")
    private Long pictureId;

    /**
     * 创建用户 id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 任务状态
     */
    @TableField(value = "taskStatus")
    private String taskStatus;

    /**
     * 扩图参数 JSON
     */
    @TableField(value = "parameters")
    private String parameters;

    /**
     * 原图 url
     */
    @TableField(value = "originalImageUrl")
    private String originalImageUrl;

    /**
     * 输出图像 url
     */
    @TableField(value = "outputImageUrl")
    private String outputImageUrl;

    /**
     * 错误码
     */
    @TableField(value = "errorCode")
    private String errorCode;

    /**
     * 错误信息
     */
    @TableField(value = "errorMessage")
    private String errorMessage;

    /**
     * 重试时关联的原任务记录 id
     */
    @TableField(value = "parentTaskId")
    private Long parentTaskId;

    /**
     * 是否已退还扩图次数（任务失败时）
     */
    @TableField(value = "quotaRefunded")
    private Integer quotaRefunded;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
