package com.my.picturesystembackend.service;

import com.my.picturesystembackend.model.entity.Picture;

/**
 * 异步服务
 */
public interface AsyncService {

    /**
     * 异步清除图片缓存
     *
     * @param pictureId 图片id
     */
    void asyncDeleteCache(Long pictureId);

    /**
     * 异步删除图片文件
     *
     * @param oldPicture 旧图片
     */
    void clearPictureFile(Picture oldPicture);
}
