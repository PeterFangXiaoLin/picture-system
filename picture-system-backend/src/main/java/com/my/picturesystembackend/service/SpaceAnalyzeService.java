package com.my.picturesystembackend.service;

import com.my.picturesystembackend.model.dto.space.analyze.*;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.space.analyze.*;

import java.util.List;

/**
 * 空间分析服务
 */
public interface SpaceAnalyzeService {

    /**
     * 获取空间资源使用分析
     *
     * @param spaceUsageAnalyzeRequest 空间资源使用分析请求
     * @param loginUser                登录用户
     * @return 空间资源使用分析响应
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片分类分析
     *
     * @param spaceCategoryAnalyzeRequest 空间图片分类请求
     * @param loginUser                   登录用户
     * @return 空间图片分类响应
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    /**
     * 获取空间标签分析
     *
     * @param spaceTagAnalyzeRequest 空间标签分析请求
     * @param loginUser 登录用户
     * @return 空间标签分析响应
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片大小分析
     *
     * @param spaceSizeAnalyzeRequest 空间图片大小分析请求
     * @param loginUser 登录用户
     * @return 空间图片大小分析响应
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * 获取空间用户分析
     *
     * @param spaceUserAnalyzeRequest 空间用户分析请求
     * @param loginUser 登录用户
     * @return 空间用户分析响应
     */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * 获取空间使用排名
     *
     * @param spaceRankAnalyzeRequest 空间使用排名请求
     * @param loginUser 登录用户
     * @return 空间使用排名响应
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
