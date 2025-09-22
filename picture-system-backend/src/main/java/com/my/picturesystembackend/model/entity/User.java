package com.my.picturesystembackend.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
@ApiModel(value = "User", description = "用户")
public class User implements Serializable {
    /**
     * id
     */
    @TableId(value = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 账号
     */
    @TableField(value = "userAccount")
    @ApiModelProperty(value = "账号")
    private String userAccount;

    /**
     * 密码
     */
    @TableField(value = "userPassword")
    @ApiModelProperty(value = "密码")
    private String userPassword;

    /**
     * 用户昵称
     */
    @TableField(value = "userName")
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    /**
     * 用户头像
     */
    @TableField(value = "userAvatar")
    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    /**
     * 用户简介
     */
    @TableField(value = "userProfile")
    @ApiModelProperty(value = "用户简介")
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @TableField(value = "userRole")
    @ApiModelProperty(value = "用户角色：user/admin")
    private String userRole;

    /**
     * 编辑时间
     */
    @TableField(value = "editTime")
    @ApiModelProperty(value = "编辑时间")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    @TableLogic
    @ApiModelProperty(value = "是否删除")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}