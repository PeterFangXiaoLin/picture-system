package com.my.picturesystembackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户通知。一条记录只属于一个接收用户。
 */
@Data
@TableName("user_notification")
public class UserNotification implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 接收用户 id */
    @TableField("userId")
    private Long userId;

    /** 通知类型 */
    @TableField("type")
    private String type;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    /** 关联业务 id，空间邀请通知中为 space_user.id */
    @TableField("relatedId")
    private Long relatedId;

    @TableField("spaceId")
    private Long spaceId;

    /** 触发通知的用户 id */
    @TableField("actorUserId")
    private Long actorUserId;

    /** 是否已读：0-未读；1-已读 */
    @TableField("isRead")
    private Integer isRead;

    /** 可操作通知的业务状态；空间邀请中：0-待确认，1-已接受，2-已拒绝 */
    @TableField("actionStatus")
    private Integer actionStatus;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
