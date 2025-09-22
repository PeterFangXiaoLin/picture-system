package com.my.picturesystembackend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求体
 */
@Data
@ApiModel(value = "DeleteRequest", description = "删除请求体")
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    private static final long serialVersionUID = 1L;
}
