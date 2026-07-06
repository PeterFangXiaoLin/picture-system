package com.my.picturesystembackend.model.dto.space.analyze;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "SpaceSizeAnalyzeRequest", description = "空间大小分析请求")
public class SpaceSizeAnalyzeRequest extends SpaceAnalyzeRequest {

}
