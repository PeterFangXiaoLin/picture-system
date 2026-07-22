package com.my.picturesystembackend.model.enums;

import lombok.Getter;

@Getter
public enum UserNotificationTypeEnum {

    SPACE_INVITE("空间邀请"),
    SPACE_INVITE_ACCEPTED("邀请已接受"),
    SPACE_INVITE_REJECTED("邀请已拒绝");

    private final String text;

    UserNotificationTypeEnum(String text) {
        this.text = text;
    }
}
