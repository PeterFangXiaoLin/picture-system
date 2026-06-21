package com.my.picturesystembackend.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 以图搜图
 */
@Data
@ApiModel(value = "SearchPictureByPictureRequest", description = "以图搜图请求")
public class SearchPictureByPictureRequest implements Serializable {

    /**
     * 图片id
     */
    @ApiModelProperty("图片id")
    private Long pictureId;

    private static final long serialVersionUID = 1L;
}
