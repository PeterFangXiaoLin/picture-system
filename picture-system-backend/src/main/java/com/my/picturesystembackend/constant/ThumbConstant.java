package com.my.picturesystembackend.constant;

/**
 * 点赞相关常量
 */
public interface ThumbConstant {

    /**
     * 点赞记录缓存key
     */
    String DO_THUMB_KEY = "doThumb:";

    /**
     * 取消点赞缓存key
     */
    String UNDO_THUMB_KEY = "undoThumb:";

    /**
     * 用户点赞缓存key前缀
     */
    String USER_THUMB_KEY_PREFIX = "thumb:";
}
