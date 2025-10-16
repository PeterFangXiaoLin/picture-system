package com.my.picturesystembackend.model.dto.picture;

import com.my.picturesystembackend.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 图片查询参数请求体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "PictureQueryRequest", description = "图片查询参数请求体")
public class PictureQueryRequest extends PageRequest implements Serializable {
  
    /**  
     * id  
     */
    @ApiModelProperty(value = "id")
    private Long id;  
  
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
     * 分类id
     */
    @ApiModelProperty(value = "分类id")
    private Long categoryId;
  
    /**  
     * 标签  
     */
    @ApiModelProperty(value = "标签")
    private List<Long> tags;
  
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
     * 搜索词（同时搜名称、简介等）  
     */
    @ApiModelProperty(value = "搜索词（同时搜名称、简介等）")
    private String searchText;  
  
    /**  
     * 用户 id  
     */
    @ApiModelProperty(value = "用户 id")
    private Long userId;  
  
    private static final long serialVersionUID = 1L;  
}
