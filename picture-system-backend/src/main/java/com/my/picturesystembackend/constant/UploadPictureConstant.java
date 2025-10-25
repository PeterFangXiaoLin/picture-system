package com.my.picturesystembackend.constant;

import java.util.Map;
import java.util.Set;

/**
 * 图片上传常量
 */
public interface UploadPictureConstant {
    /**
     * 单文件大小限制，1MB
     */
    long ONE_MB = 1024 * 1024L;

    /**
     * 允许的文件格式
     */
    Set<String> ALLOW_FILE_FORMAT = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

    /**
     * 允许的文件类型
     */
    Set<String> ALLOW_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/gif", "image/webp", "image/jpg");

    /**
     * 文件类型和文件格式的映射
     */
    Map<String, String> CONTENT_TYPE_TO_FILE_FORMAT = Map.of(
            "image/png", "png",
            "image/jpeg", "jpg",
            "image/gif", "gif",
            "image/webp", "webp",
            "image/jpg", "jpg"
    );

    /**
     * 最大上传图片数量
     */
    int MAX_UPLOAD_PICTURE_COUNT = 30;
}
