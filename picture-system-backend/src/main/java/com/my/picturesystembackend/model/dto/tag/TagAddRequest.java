package com.my.picturesystembackend.model.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 标签添加请求体
 */
@Data
@ApiModel(value = "TagAddRequest", description = "标签添加请求体")
public class TagAddRequest implements Serializable {

    private static final long serialVersionUID = -4576199663229719928L;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    @NotBlank
    private String name;
}
