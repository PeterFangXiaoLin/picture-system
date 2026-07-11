package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.my.picturesystembackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.my.picturesystembackend.model.entity.SpaceUser;
import com.my.picturesystembackend.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author hello
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2026-07-08 10:02:53
*/
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest 添加空间成员请求
     * @return 添加成功id
     */
    Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验参数
     *
     * @param spaceUser 空间成员
     * @param add 是否添加
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取查询条件
     *
     * @param spaceUserQueryRequest 查询空间成员请求
     * @return 查询条件
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取封装对象
     *
     * @param spaceUser pojo
     * @param request request
     * @return vo
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取封装对象列表
     *
     * @param spaceUserList pojo 列表
     * @return vo 列表
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
