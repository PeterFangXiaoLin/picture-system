package com.my.picturesystembackend.model.dto.picture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量上传图片请求参数
 */
@Data
@ApiModel(value = "PictureUploadByBatchRequest", description = "批量上传图片请求参数")
public class PictureUploadByBatchRequest implements Serializable {

    private static final long serialVersionUID = 5876965710699391630L;

    /**
     * 搜索词  
     */
    @ApiModelProperty(value = "搜索词")
    private String searchText;

    /**
     * 名称前缀
     */
    @ApiModelProperty(value = "名称前缀")
    private String namePrefix;

    /**  
     * 抓取数量  
     */
    @ApiModelProperty(value = "抓取数量")
    private Integer count = 10;

    /**
     * 起始偏移量
     */
    @ApiModelProperty(value = "起始偏移量")
    private Integer first;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类id")
    private Long categoryId;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private List<Long> tags;
}
