package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.model.dto.space.SpaceAddRequest;
import com.my.picturesystembackend.model.dto.space.SpaceEditRequest;
import com.my.picturesystembackend.model.dto.space.SpaceQueryRequest;
import com.my.picturesystembackend.model.dto.space.SpaceUpdateRequest;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author helloworld
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-12-04 00:18:30
*/
public interface SpaceService extends IService<Space> {

    /**
     * 校验空间是否合法
     * @param space 空间信息
     * @param add 是否为添加操作
     */
    void validSpace(Space space, boolean add);

    /**
     * 根据空间级别填充空间信息
     * @param space 空间信息
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 添加空间
     *
     * @param spaceAddRequest 添加空间请求体
     * @param request request
     * @return 新空间id
     */
    Long addSpace(SpaceAddRequest spaceAddRequest, HttpServletRequest request);

    /**
     * 更新空间信息
     * @param spaceUpdateRequest 更新空间请求体
     * @return 是否更新成功
     */
    Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest);

    /**
     * 获取空间封装
     * @param space 空间信息
     * @param request request
     * @return SpaceVO
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间封装分页
     * @param spacePage 空间分页
     * @param request request
     * @return SpaceVO分页
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取空间查询包装器
     * @param spaceQueryRequest 空间查询请求体
     * @return QueryWrapper
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 删除空间
     *
     * @param deleteRequest 删除请求体
     * @param request request
     * @return 是否删除成功
     */
    Boolean deleteSpace(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 编辑空间
     *
     * @param spaceEditRequest 编辑空间请求体
     * @param request request
     * @return 是否编辑成功
     */
    Boolean editSpace(SpaceEditRequest spaceEditRequest, HttpServletRequest request);
}
