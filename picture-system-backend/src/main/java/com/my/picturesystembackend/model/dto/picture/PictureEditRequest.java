package com.my.picturesystembackend.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片编辑请求体
 */
@Data
@ApiModel(value = "PictureEditRequest", description = "图片编辑请求体")
public class PictureEditRequest implements Serializable {
  
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
     * 分类  
     */
    @ApiModelProperty(value = "分类id")
    private Long categoryId;
  
    /**  
     * 标签  
     */
    @ApiModelProperty(value = "标签")
    private List<Long> tags;
  
    private static final long serialVersionUID = 1L;  
}
