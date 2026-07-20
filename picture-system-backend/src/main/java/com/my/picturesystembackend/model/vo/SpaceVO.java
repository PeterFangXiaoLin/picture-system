package com.my.picturesystembackend.model.vo;

import com.my.picturesystembackend.model.entity.Space;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "SpaceVO", description = "空间")
public class SpaceVO implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 空间名称
     */
    @ApiModelProperty(value = "空间名称")
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    @ApiModelProperty(value = "空间级别：0-普通版 1-专业版 2-旗舰版")
    private Integer spaceLevel;

    /**
     * 空间类型：0-私有空间，1-团队空间
     */
    @ApiModelProperty(value = "空间类型：0-私有空间，1-团队空间")
    private Integer spaceType;

    /**
     * 空间图片的最大总大小
     */
    @ApiModelProperty(value = "空间图片的最大总大小")
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    @ApiModelProperty(value = "空间图片的最大数量")
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    @ApiModelProperty(value = "当前空间下图片的总大小")
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    @ApiModelProperty(value = "当前空间下的图片数量")
    private Long totalCount;

    /**
     * 创建用户 id
     */
    @ApiModelProperty(value = "创建用户 id")
    private Long userId;

    /**
     * 权限列表
     */
    @ApiModelProperty(value = "权限列表")
    private List<String> permissionList;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    @ApiModelProperty(value = "编辑时间")
    private LocalDateTime editTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建用户信息
     */
    @ApiModelProperty(value = "创建用户信息")
    private UserVO user;

    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     *
     * @param spaceVO 空间VO
     * @return space
     */
    public static Space voToObj(SpaceVO spaceVO) {
        if (spaceVO == null) {
            return null;
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceVO, space);
        return space;
    }

    /**
     * 对象转封装类
     *
     * @param space 空间
     * @return spaceVO
     */
    public static SpaceVO objToVo(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtils.copyProperties(space, spaceVO);
        return spaceVO;
    }
}
