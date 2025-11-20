package com.my.picturesystembackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum PictureFileSuffixEnum {

    JPG("JPG图片", "jpg"),
    JPEG("JPEG图片", "jpeg"),
    PNG("PNG图片", "png"),
    GIF("GIF图片", "gif"),
    BMP("BMP图片", "bmp"),
    WEBP("WEBP图片", "webp"),
    SVG("SVG图片", "svg");

    private final String text;

    private final String value;

    PictureFileSuffixEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static PictureFileSuffixEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureFileSuffixEnum anEnum : PictureFileSuffixEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}