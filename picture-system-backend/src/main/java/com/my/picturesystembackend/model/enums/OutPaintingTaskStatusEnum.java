package com.my.picturesystembackend.model.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * AI 扩图任务状态
 */
@Getter
public enum OutPaintingTaskStatusEnum {

    PENDING("排队中", "PENDING"),
    RUNNING("处理中", "RUNNING"),
    SUSPENDED("挂起", "SUSPENDED"),
    SUCCEEDED("执行成功", "SUCCEEDED"),
    FAILED("执行失败", "FAILED"),
    UNKNOWN("未知", "UNKNOWN");

    private final String text;
    private final String value;

    OutPaintingTaskStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static OutPaintingTaskStatusEnum getEnumByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        for (OutPaintingTaskStatusEnum statusEnum : OutPaintingTaskStatusEnum.values()) {
            if (statusEnum.value.equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 是否为终态
     */
    public static boolean isFinished(String status) {
        return SUCCEEDED.value.equals(status) || FAILED.value.equals(status) || UNKNOWN.value.equals(status);
    }
}
