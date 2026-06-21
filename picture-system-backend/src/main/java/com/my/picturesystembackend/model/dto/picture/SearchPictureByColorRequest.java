package com.my.picturesystembackend.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片颜色搜索
 */
@Data
@ApiModel(value = "SearchPictureByColorRequest", description = "图片颜色搜索")
public class SearchPictureByColorRequest implements Serializable {

    /**
     * 图片主色调
     */
    @ApiModelProperty("图片主色调")
    private String picColor;

    /**
     * 空间 id
     */
    @ApiModelProperty("空间id")
    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
