package com.my.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.common.DeleteRequest;
import com.my.controller.vo.picture.*;
import com.my.domain.entity.Picture;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author helloworld
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-15 11:52:27
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadReqVO
     * @param request
     * @return
     */
    PictureRespVO uploadPicture(MultipartFile multipartFile, PictureUploadReqVO pictureUploadReqVO, HttpServletRequest request);

    /**
     * 获取图片包装类
     *
     * @param picture
     * @return
     */
    PictureRespVO getPictureRespVO(Picture picture);

    /**
     * 获取图片包装类分页
     *
     * @param picturePage
     * @return
     */
    Page<PictureRespVO> getPictureRespVOPage(Page<Picture> picturePage);

    /**
     * 删除照片
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    Boolean deletePicture(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新照片 （管理员）
     *
     * @param pictureUpdateReqVO
     * @return
     */
    Boolean updatePicture(PictureUpdateReqVO pictureUpdateReqVO);

    /**
     * 根据id获取图片
     *
     * @param id
     * @return
     */
    Picture getPictureById(Long id);

    /**
     * 根据id获取图片包装类
     *
     * @param id
     * @return
     */
    PictureRespVO getPictureVOById(Long id);

    /**
     * 分页获取图片
     *
     * @param pictureQueryReqVO
     * @return
     */
    Page<Picture> listPictureByPage(PictureQueryReqVO pictureQueryReqVO);

    /**
     * 拼接分页查询条件
     *
     * @param pictureQueryReqVO
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryReqVO pictureQueryReqVO);

    /**
     * 分页获取图片vo
     *
     * @param pictureQueryReqVO
     * @return
     */
    Page<PictureRespVO> listPictureRespVOByPage(PictureQueryReqVO pictureQueryReqVO);

    /**
     * 编辑图片
     *
     * @param pictureEditReqVO
     * @param request
     * @return
     */
    Boolean editPicture(PictureEditReqVO pictureEditReqVO, HttpServletRequest request);
}
