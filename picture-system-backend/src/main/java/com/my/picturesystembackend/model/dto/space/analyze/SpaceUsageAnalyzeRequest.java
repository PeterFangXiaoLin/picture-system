package com.my.picturesystembackend.model.dto.space.analyze;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 空间资源使用分析请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "SpaceUsageAnalyzeRequest", description = "空间资源使用分析请求")
public class SpaceUsageAnalyzeRequest extends SpaceAnalyzeRequest {

}
