package com.my.picturesystembackend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 扩图剩余次数
 */
@Data
@ApiModel(value = "OutPaintingQuotaVO", description = "AI 扩图剩余次数")
public class OutPaintingQuotaVO implements Serializable {

    @ApiModelProperty(value = "剩余次数")
    private Integer remaining;

    @ApiModelProperty(value = "是否不限次数（管理员）")
    private Boolean unlimited;

    private static final long serialVersionUID = 1L;
}
