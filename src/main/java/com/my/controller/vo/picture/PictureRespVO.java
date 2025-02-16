package com.my.controller.vo.picture;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.my.controller.vo.user.UserRespVO;
import com.my.domain.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureRespVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private List<String> tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserRespVO userRespVO;

    /**
     * 包装类转 数据库实体类
     *
     * @param pictureRespVO
     * @return
     */
    public static Picture voToDo(PictureRespVO pictureRespVO) {
        if (ObjUtil.isNull(pictureRespVO)) {
            return null;
        }
        Picture picture = BeanUtil.toBean(pictureRespVO, Picture.class);
        // 转化标签字段
        picture.setTags(JSONUtil.toJsonStr(pictureRespVO.getTags()));
        return picture;
    }

    /**
     * 数据库实体类转 包装类
     *
     * @param picture
     * @return
     */
    public static PictureRespVO doToVo(Picture picture) {
        if (ObjUtil.isNull(picture)) {
            return null;
        }
        PictureRespVO pictureRespVO = BeanUtil.toBean(picture, PictureRespVO.class);
        // 转化标签字段
        pictureRespVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureRespVO;
    }
}
