package com.my.picturesystembackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片管理员视图对象（包含更多管理字段）
 */
@Data
@ApiModel(value = "PictureAdminVO", description = "图片管理员VO")
public class PictureAdminVO implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 图片 url
     */
    @ApiModelProperty(value = "图片 url")
    private String url;

    /**
     * 图片名称
     */
    @ApiModelProperty(value = "图片名称")
    private String name;

    /**
     * 简介
     */
    @ApiModelProperty(value = "简介")
    private String introduction;

    /**
     * 标签ids
     */
    @ApiModelProperty(value = "标签ids")
    private String tags;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签VO")
    private List<TagVO> tagVOList;

    /**
     * 分类 id
     */
    @ApiModelProperty(value = "分类 id")
    private Long categoryId;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类")
    private CategoryVO category;

    /**
     * 文件体积
     */
    @ApiModelProperty(value = "文件体积")
    private Long picSize;

    /**
     * 图片宽度
     */
    @ApiModelProperty(value = "图片宽度")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @ApiModelProperty(value = "图片高度")
    private Integer picHeight;

    /**
     * 图片比例
     */
    @ApiModelProperty(value = "图片比例")
    private Double picScale;

    /**
     * 图片格式
     */
    @ApiModelProperty(value = "图片格式")
    private String picFormat;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id")
    private Long userId;

    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    @ApiModelProperty(value = "状态：0-待审核; 1-通过; 2-拒绝")
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    @ApiModelProperty(value = "审核信息")
    private String reviewMessage;

    /**
     * 审核人 id
     */
    @ApiModelProperty(value = "审核人 id")
    private Long reviewerId;

    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime reviewTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    @ApiModelProperty(value = "编辑时间")
    private LocalDateTime editTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除（管理员字段）
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDelete;

    /**
     * 创建用户信息
     */
    @ApiModelProperty(value = "创建用户信息")
    private UserVO user;

    private static final long serialVersionUID = 1L;
}

