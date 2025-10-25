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
}
