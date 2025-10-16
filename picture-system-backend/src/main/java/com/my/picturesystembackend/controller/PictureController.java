package com.my.picturesystembackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.picturesystembackend.annotation.AuthCheck;
import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.constant.UserConstant;
import com.my.picturesystembackend.model.dto.picture.PictureEditRequest;
import com.my.picturesystembackend.model.dto.picture.PictureQueryRequest;
import com.my.picturesystembackend.model.dto.picture.PictureUpdateRequest;
import com.my.picturesystembackend.model.dto.picture.PictureUploadRequest;
import com.my.picturesystembackend.model.vo.PictureAdminVO;
import com.my.picturesystembackend.model.vo.PictureVO;
import com.my.picturesystembackend.service.PictureService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/picture")
@RequiredArgsConstructor
@Slf4j
public class PictureController {

    private final PictureService pictureService;

    /**
     * 上传图片
     *
     * @param multipartFile file
     * @param pictureUploadRequest 图片上传请求体
     * @param request request
     * @return 图片信息VO
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "上传图片", notes = "上传图片")
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file")MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        return ResultUtils.success(pictureService.uploadPicture(multipartFile, pictureUploadRequest, request));
    }

    /**
     * 删除图片
     *
     * @param deleteRequest 删除图片请求
     * @param request request
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除图片", notes = "删除图片")
    public BaseResponse<Boolean> deletePicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(pictureService.deletePicture(deleteRequest, request));
    }

    /**
     * 更新图片 (仅管理员可用)
     *
     * @param pictureUpdateRequest 图片更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新图片", notes = "更新图片")
    public BaseResponse<Boolean> updatePicture(PictureUpdateRequest pictureUpdateRequest) {
        return ResultUtils.success(pictureService.updatePicture(pictureUpdateRequest));
    }

    /**
     * 根据id获取图片（仅管理员可用）
     *
     * @param id 图片id
     * @return 图片信息VO
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "根据id获取图片（仅管理员可用）", notes = "获取图片")
    public BaseResponse<PictureAdminVO> getPictureAdminVOById(Long id) {
        return ResultUtils.success(pictureService.getPictureAdminVOById(id));
    }

    /**
     * 根据id获取图片VO
     *
     * @param id id
     * @return 图片VO
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据id获取图片", notes = "获取图片")
    public BaseResponse<PictureVO> getPictureVOById(Long id) {
        return ResultUtils.success(pictureService.getPictureVOById(id));
    }

    /**
     * 分页获取图片列表（仅管理员可用）
     *
     * @param pictureQueryRequest 分页获取图片列表请求
     * @return 图片列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取图片列表（仅管理员可用）", notes = "分页获取图片列表")
    public BaseResponse<Page<PictureAdminVO>> listPictureAdminVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        return ResultUtils.success(pictureService.listPictureAdminVOByPage(pictureQueryRequest));
    }

    /**
     * 分页获取图片列表
     *
     * @param pictureQueryRequest 分页获取图片列表请求
     * @return 图片列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取图片列表", notes = "分页获取图片列表")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        return ResultUtils.success(pictureService.listPictureVOByPage(pictureQueryRequest));
    }

    /**
     * 编辑图片（给用户使用）
     *
     * @param pictureEditRequest 图片更新请求
     * @param request request
     * @return 是否更新成功
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑图片（给用户使用）", notes = "编辑图片")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        return ResultUtils.success(pictureService.editPicture(pictureEditRequest, request));
    }
}
