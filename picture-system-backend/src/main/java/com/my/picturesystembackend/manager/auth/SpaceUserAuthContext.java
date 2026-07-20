package com.my.picturesystembackend.manager.auth;

import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.SpaceUser;
import lombok.Data;

/**
 * SpaceUserAuthContext
 * 表示用户在特定空间内的授权上下文，包括关联的图片、空间和用户信息。
 */
@Data
public class SpaceUserAuthContext {

    public static SpaceUserAuthContext ofPicture(Picture picture) {
        SpaceUserAuthContext context = new SpaceUserAuthContext();
        context.setPicture(picture);
        return context;
    }

    public static SpaceUserAuthContext ofPictureAndSpace(Picture picture, Space space) {
        SpaceUserAuthContext context = ofPicture(picture);
        context.setSpace(space);
        return context;
    }

    public static SpaceUserAuthContext ofSpace(Space space) {
        SpaceUserAuthContext context = new SpaceUserAuthContext();
        context.setSpace(space);
        return context;
    }

    public static SpaceUserAuthContext ofSpaceId(Long spaceId) {
        SpaceUserAuthContext context = new SpaceUserAuthContext();
        context.setSpaceId(spaceId);
        return context;
    }

    public static SpaceUserAuthContext ofSpaceUser(SpaceUser spaceUser) {
        SpaceUserAuthContext context = new SpaceUserAuthContext();
        context.setSpaceUser(spaceUser);
        return context;
    }

    public static SpaceUserAuthContext ofSpaceUserAndSpace(SpaceUser spaceUser, Space space) {
        SpaceUserAuthContext context = ofSpaceUser(spaceUser);
        context.setSpace(space);
        return context;
    }

    /**
     * 临时参数，不同请求对应的 id 可能不同
     */
    private Long id;

    /**
     * 图片 ID
     */
    private Long pictureId;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 空间用户 ID
     */
    private Long spaceUserId;

    /**
     * 图片信息
     */
    private Picture picture;

    /**
     * 空间信息
     */
    private Space space;

    /**
     * 空间用户信息
     */
    private SpaceUser spaceUser;
}
