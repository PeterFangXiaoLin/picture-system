package com.my.picturesystembackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 空间成员邀请确认状态。
 */
@Getter
public enum SpaceUserInviteStatusEnum {

    PENDING("待确认", 0),
    ACCEPTED("已接受", 1),
    REJECTED("已拒绝", 2);

    private final String text;

    private final int value;

    SpaceUserInviteStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static SpaceUserInviteStatusEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceUserInviteStatusEnum statusEnum : values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
