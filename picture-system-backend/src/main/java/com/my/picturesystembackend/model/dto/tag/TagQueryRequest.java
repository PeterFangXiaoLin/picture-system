package com.my.picturesystembackend.model.dto.tag;

import com.my.picturesystembackend.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 标签查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TagQueryRequest", description = "标签查询请求")
public class TagQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 7794209799611777145L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    private String name;

    /**
     * 创建用户 id
     */
    @ApiModelProperty(value = "创建用户 id")
    private Long userId;
}
