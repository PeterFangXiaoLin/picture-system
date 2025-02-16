package com.my.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.annotation.AuthCheck;
import com.my.common.BaseResponse;
import com.my.common.DeleteRequest;
import com.my.constant.UserConstant;
import com.my.controller.vo.picture.*;
import com.my.domain.entity.Picture;
import com.my.service.PictureService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static com.my.common.ResultUtils.success;

@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private PictureService pictureService;

    /**
     * 上传图片（可重新上传）
     *
     * @param multipartFile
     * @param pictureUploadReqVO
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureRespVO> uploadPicture(
            @RequestPart("file")MultipartFile multipartFile,
            PictureUploadReqVO pictureUploadReqVO,
            HttpServletRequest request) {
        return success(pictureService.uploadPicture(multipartFile, pictureUploadReqVO, request));
    }

    /**
     * 删除图片
     *
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return success(pictureService.deletePicture(deleteRequest, request));
    }

    /**
     * 更新图片 （管理员）
     *
     * @param pictureUpdateReqVO
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateReqVO pictureUpdateReqVO) {
        return success(pictureService.updatePicture(pictureUpdateReqVO));
    }

    /**
     * 根据id 获取图片 （仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(@RequestParam("id") Long id) {
        return success(pictureService.getPictureById(id));
    }

    /**
     * 获取图片封装类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureRespVO> getPictureRespVOById(@RequestParam("id") Long id) {
        return success(pictureService.getPictureVOById(id));
    }

    /**
     * 分页获取图片列表（仅管理员）
     *
     * @param pictureQueryReqVO
     * @return
     */
    @PostMapping("/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryReqVO pictureQueryReqVO) {
        return success(pictureService.listPictureByPage(pictureQueryReqVO));
    }

    /**
     * 分页获取图片 vo
     *
     * @param pictureQueryReqVO
     * @return
     */
    @PostMapping("/page/vo")
    public BaseResponse<Page<PictureRespVO>> listPictureRespVOByPage(@RequestBody PictureQueryReqVO pictureQueryReqVO) {
        return success(pictureService.listPictureRespVOByPage(pictureQueryReqVO));
    }

    /**
     * 编辑图片
     *
     * @param pictureEditReqVO
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditReqVO pictureEditReqVO, HttpServletRequest request) {
        return success(pictureService.editPicture(pictureEditReqVO, request));
    }

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return success(pictureTagCategory);
    }
}
