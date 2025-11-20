package com.my.picturesystembackend.model.dto.thumb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 点赞请求参数
 */
@Data
@ApiModel(value = "DoThumbRequest", description = "点赞请求参数")
public class DoThumbRequest implements Serializable {

    private static final long serialVersionUID = 2366723337233321344L;

    /**
     * 图片id
     */
    @ApiModelProperty(value = "图片id")
    private Long pictureId;
}
