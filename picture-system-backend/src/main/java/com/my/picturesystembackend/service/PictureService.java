package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.model.dto.picture.PictureEditRequest;
import com.my.picturesystembackend.model.dto.picture.PictureQueryRequest;
import com.my.picturesystembackend.model.dto.picture.PictureUpdateRequest;
import com.my.picturesystembackend.model.dto.picture.PictureUploadRequest;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.vo.PictureAdminVO;
import com.my.picturesystembackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

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
     * @param multipartFile file
     * @param pictureUploadRequest request
     * @param request request
     * @return PictureVO
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
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
     * @return 是否更新成功
     */
    boolean updatePicture(PictureUpdateRequest pictureUpdateRequest);

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
     * 编辑图片
     *
     * @param pictureEditRequest 编辑图片请求
     * @param request request
     * @return 是否编辑成功
     */
    boolean editPicture(PictureEditRequest pictureEditRequest, HttpServletRequest request);
}
