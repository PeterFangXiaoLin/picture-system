package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.model.dto.thumb.DoThumbRequest;
import com.my.picturesystembackend.model.entity.Thumb;

import javax.servlet.http.HttpServletRequest;

/**
* @author helloworld
* @description 针对表【thumb(点赞记录表)】的数据库操作Service
* @createDate 2025-10-29 14:59:09
*/
public interface ThumbService extends IService<Thumb> {

    /**
     * 点赞
     *
     * @param doThumbRequest 点赞请求
     * @param request 请求
     * @return 是否成功
     */
    boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    /**
     * 取消点赞
     *
     * @param doThumbRequest 点赞请求
     * @param request 请求
     * @return 是否成功
     */
    boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    /**
     * 判断用户是否点赞
     *
     * @param pictureId 图片id
     * @param userId 用户id
     * @return 是否点赞
     */
    boolean hasThumb(Long pictureId, Long userId);
}
