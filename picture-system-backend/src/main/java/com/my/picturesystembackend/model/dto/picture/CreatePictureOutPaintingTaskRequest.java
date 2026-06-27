package com.my.picturesystembackend.model.dto.picture;

import com.my.picturesystembackend.api.aliyunai.model.CreateOutPaintingTaskRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建扩图请求
 */
@Data
@ApiModel(value = "CreatePictureOutPaintingTaskRequest", description = "扩图请求dto")
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}
