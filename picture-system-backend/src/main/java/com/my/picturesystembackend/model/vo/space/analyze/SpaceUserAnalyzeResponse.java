package com.my.picturesystembackend.model.vo.space.analyze;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SpaceUserAnalyzeResponse", description = "空间用户上传分析响应")
public class SpaceUserAnalyzeResponse implements Serializable {

    /**
     * 时间区间
     */
    @ApiModelProperty(value = "时间区间")
    private String period;

    /**
     * 上传数量
     */
    @ApiModelProperty(value = "上传数量")
    private Long count;

    private static final long serialVersionUID = 1L;
}
