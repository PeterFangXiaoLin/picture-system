package com.my.picturesystembackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PictureVO
 */
@Data
@ApiModel(value = "PictureVO", description = "图片VO")
public class PictureVO implements Serializable {
  
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
    @ApiModelProperty(value = "标签")
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
     * 创建用户信息  
     */
    @ApiModelProperty(value = "创建用户信息")
    private UserVO user;  

    private static final long serialVersionUID = 1L;
}
