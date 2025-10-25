package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.model.dto.picture.*;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.PictureAdminVO;
import com.my.picturesystembackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;

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
     * @param id id
     * @return 图片VO
     */
    PictureVO getPictureVOById(Long id);

    /**
     * picture TO VO
     *
     * @param picture picture
     * @return PictureVO
     */
    PictureVO getPictureVO(Picture picture);

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
     * @return pictureAdminVO list
     */
    Page<PictureAdminVO> listPictureAdminVOByPage(PictureQueryRequest pictureQueryRequest);

    /**
     * 分页获取 pictureVO
     *
     * @param pictureQueryRequest pictureQueryRequest
     * @return pictureVO list
     */
    Page<PictureVO> listPictureVOByPage(PictureQueryRequest pictureQueryRequest);

    /**
     * 分页获取 pictureVO 缓存版
     *
     * @param pictureQueryRequest pictureQueryRequest
     * @return pictureVO list
     */
    Page<PictureVO> listPictureVOByPageWithCache(PictureQueryRequest pictureQueryRequest);

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
     * 异步清除图片缓存
     *
     * @param pictureId 图片id
     */
    void asyncDeleteCache(Long pictureId);
}
