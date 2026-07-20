package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.model.dto.picture.*;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.PictureAdminVO;
import com.my.picturesystembackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
* @author helloworld
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-10-11 19:19:46
*/
public interface PictureService extends IService<Picture> {

    /**
     * 校验图片
     * @param picture 图片
     */
    void validPicture(Picture picture);

    /**
     * 上传图片
     *
     * @param inputSource 输入源
     * @param pictureUploadRequest request
     * @param request              request
     * @return PictureVO
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            HttpServletRequest request);

    /**
     * 删除图片
     *
     * @param deleteRequest 删除图片请求
     * @param request request
     * @return 是否删除成功
     */
    boolean deletePicture(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新图片
     *
     * @param pictureUpdateRequest 更新图片请求
     * @param request request
     * @return 是否更新成功
     */
    boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request);

    /**
     * 获取图片管理员封装
     * @param id id
     * @return PictureAdminVO
     */
    PictureAdminVO getPictureAdminVOById(Long id);

    /**
     * 根据id获取图片VO
     *
     * @param id id
     * @param request request
     * @return 图片VO
     */
    PictureVO getPictureVOById(Long id, HttpServletRequest request);

    /**
     * 获取封装的pictureVO
     *
     * @param picture picture
     * @param loginUser loginUser
     * @return pictureVO
     */
    PictureVO getPictureVO(Picture picture, User loginUser);

    /**
     * 获取封装的 PictureVO
     *
     * @param picture picture
     * @param loginUser loginUser
     * @param space space
     * @return pictureVO
     */
    PictureVO getPictureVO(Picture picture, User loginUser, Space space);

    /**
     * pictureQueryRequest TO QueryWrapper
     * @param pictureQueryRequest pictureQueryRequest
     * @return QueryWrapper
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 分页获取 pictureAdminVO
     *
     * @param pictureQueryRequest pictureQueryRequest
     * @param request request
     * @return pictureAdminVO list
     */
    Page<PictureAdminVO> listPictureAdminVOByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    /**
     * 分页获取 pictureVO
     *
     * @param pictureQueryRequest pictureQueryRequest
     * @param request request
     * @return pictureVO list
     */
    Page<PictureVO> listPictureVOByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    /**
     * 分页获取 pictureVO 缓存版
     *
     * @param pictureQueryRequest pictureQueryRequest
     * @param request request
     * @return pictureVO list
     */
    Page<PictureVO> listPictureVOByPageWithCache(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    /**
     * 编辑图片
     *
     * @param pictureEditRequest 编辑图片请求
     * @param request request
     * @return 是否编辑成功
     */
    boolean editPicture(PictureEditRequest pictureEditRequest, HttpServletRequest request);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest 图片审核请求
     * @param request request
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, HttpServletRequest request);

    /**
     * 填充审核参数
     * 图片上传，用户编辑，管理员更新都需要重置审核参数
     *
     * @param picture picture
     * @param loginUser loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量上传图片
     *
     * @param pictureUploadByBatchRequest 批量上传图片请求
     * @param request request
     * @return 批量上传图片数量
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, HttpServletRequest request);

    /**
     * 清除图片缓存
     *
     * @param pictureId 图片id
     */
    void clearPictureCache(Long pictureId);

    /**
     * 检查图片操作权限
     *
     * @param loginUser 登录用户
     * @param picture   图片
     */
    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 判断是否是热门数据
     *
     * @param localDateTime 时间
     * @return 是否是热门数据
     */
    boolean isHotData(LocalDateTime localDateTime);

    /**
     * 获取图片创建时间
     *
     * @param pictureId 图片id
     * @return 图片创建时间
     */
    LocalDateTime getPictureCreateTime(Long pictureId);

    /**
     * 以颜色搜图（只查空间内的）
     *
     * @param spaceId   空间id
     * @param picColor  颜色
     * @param loginUser 登录的用户
     * @return 图片列表
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 获取图片主色调
     *
     * @param pictureUrl 图片 URL
     * @return 主色调，格式 0xRRGGBB
     */
    String getPicturePicColor(String pictureUrl);

    /**
     * 批量编辑图片
     *
     * @param pictureEditByBatchRequest 批量编辑图片请求
     * @param loginUser                 登录用户
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);
}
