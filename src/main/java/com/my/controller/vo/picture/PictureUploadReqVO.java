package com.my.controller.vo.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadReqVO implements Serializable {

    /**
     * 图片 id（用于修改）
     */
    private Long id;

}
