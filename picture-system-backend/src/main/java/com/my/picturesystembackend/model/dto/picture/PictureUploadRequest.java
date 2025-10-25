package com.my.picturesystembackend.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传请求体
 */
@Data
@ApiModel(value = "PictureUploadRequest", description = "图片上传请求体")
public class PictureUploadRequest implements Serializable {
  
    /**  
     * 图片 id（用于修改）  
     */
    @ApiModelProperty(value = "图片 id（用于修改）")
    private Long id;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String fileUrl;

    private static final long serialVersionUID = 1L;  
}
