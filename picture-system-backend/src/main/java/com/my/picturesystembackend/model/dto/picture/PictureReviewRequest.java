package com.my.picturesystembackend.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片审核参数请求体
 */
@Data
@ApiModel(value = "PictureReviewRequest", description = "图片审核参数请求体")
public class PictureReviewRequest implements Serializable {
  
    /**  
     * id  
     */
    @ApiModelProperty(value = "id")
    private Long id;  
  
    /**  
     * 状态：0-待审核, 1-通过, 2-拒绝  
     */
    @ApiModelProperty(value = "状态：0-待审核, 1-通过, 2-拒绝")
    private Integer reviewStatus;  
  
    /**  
     * 审核信息  
     */
    @ApiModelProperty(value = "审核信息")
    private String reviewMessage;  
  
  
    private static final long serialVersionUID = 1L;  
}
