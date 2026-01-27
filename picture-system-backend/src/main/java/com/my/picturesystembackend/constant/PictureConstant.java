package com.my.picturesystembackend.constant;

/**
 * 图片常量
 */
public interface PictureConstant {
    /**
     * 管理员自动过审
     */
    String ADMIN_AUTO_REVIEW_PASS = "管理员自动过审";

    /**
     * Redis key 前缀
     */
    String REDIS_KEY_PREFIX = "picture:listPictureVOByPage:";

    /**
     * 热数据天数 30天
     */
    int HOT_DATA_DAYS = 30;

    /**
     * 每批次存储的图片数量（用于hash分桶，避免单个hash过大）
     */
    int BATCH_SIZE = 10000;

    /**
     * 图片元数据（批量存储，减少key数量）
     * picture:meta:batch:{batchId}  field: pictureId  value: createTime
     */
    String PICTURE_META_BATCH_PREFIX = "picture:meta:batch:";
}
