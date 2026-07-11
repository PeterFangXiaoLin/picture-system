package com.my.picturesystembackend.model.vo;

import com.my.picturesystembackend.model.entity.SpaceUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 空间成员 VO
 */
@Data
@ApiModel(value = "SpaceUserVO", description = "空间成员 VO")
public class SpaceUserVO implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 空间 id
     */
    @ApiModelProperty(value = "空间 id")
    private Long spaceId;

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    @ApiModelProperty(value = "空间角色：viewer/editor/admin")
    private String spaceRole;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 用户信息
     */
    @ApiModelProperty(value = "用户信息")
    private UserVO user;

    /**
     * 空间信息
     */
    @ApiModelProperty(value = "空间信息")
    private SpaceVO space;

    private static final long serialVersionUID = -3423170384043598252L;

    /**
     * 封装类转对象
     *
     * @param spaceUserVO vo
     * @return pojo
     */
    public static SpaceUser voToObj(SpaceUserVO spaceUserVO) {
        if (spaceUserVO == null) {
            return null;
        }
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserVO, spaceUser);
        return spaceUser;
    }

    /**
     * 对象转封装类
     *
     * @param spaceUser pojo
     * @return vo
     */
    public static SpaceUserVO objToVo(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }
        SpaceUserVO spaceUserVO = new SpaceUserVO();
        BeanUtils.copyProperties(spaceUser, spaceUserVO);
        return spaceUserVO;
    }
}
