package com.my.picturesystembackend.model.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上传图片请求
 */
@Data
@ApiModel(value = "UploadPictureResult", description = "上传图片请求")
public class UploadPictureResult {  
  
    /**  
     * 图片地址  
     */
    @ApiModelProperty(value = "图片地址")
    private String url;  
  
    /**  
     * 图片名称  
     */
    @ApiModelProperty(value = "图片名称")
    private String picName;  
  
    /**  
     * 文件体积  
     */
    @ApiModelProperty(value = "文件体积")
    private Long picSize;  
  
    /**  
     * 图片宽度  
     */
    @ApiModelProperty(value = "图片宽度")
    private int picWidth;  
  
    /**  
     * 图片高度  
     */
    @ApiModelProperty(value = "图片高度")
    private int picHeight;  
  
    /**  
     * 图片宽高比  
     */
    @ApiModelProperty(value = "图片宽高比")
    private Double picScale;
  
    /**  
     * 图片格式  
     */
    @ApiModelProperty(value = "图片格式")
    private String picFormat;
  
}
