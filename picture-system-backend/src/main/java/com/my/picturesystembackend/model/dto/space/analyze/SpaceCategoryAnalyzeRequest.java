package com.my.picturesystembackend.model.dto.space.analyze;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 空间图片分类请求体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "SpaceCategoryAnalyzeRequest", description = "空间图片分类请求体")
public class SpaceCategoryAnalyzeRequest extends SpaceAnalyzeRequest {

}
