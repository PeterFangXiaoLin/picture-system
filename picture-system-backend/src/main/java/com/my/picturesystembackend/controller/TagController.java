package com.my.picturesystembackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.picturesystembackend.annotation.AuthCheck;
import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.constant.UserConstant;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.tag.TagAddRequest;
import com.my.picturesystembackend.model.dto.tag.TagQueryRequest;
import com.my.picturesystembackend.model.dto.tag.TagUpdateRequest;
import com.my.picturesystembackend.model.entity.Tag;
import com.my.picturesystembackend.model.vo.TagVO;
import com.my.picturesystembackend.service.TagService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 添加标签
     * @param tagAddRequest 添加标签请求体
     * @param request request
     * @return 新标签 id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "添加标签", notes = "添加标签")
    public BaseResponse<Long> addTag(@RequestBody TagAddRequest tagAddRequest, HttpServletRequest request) {
        return ResultUtils.success(tagService.addTag(tagAddRequest, request));
    }

    /**
     * 删除标签
     * @param deleteRequest 删除请求体
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "删除标签", notes = "删除标签")
    public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(tagService.deleteTag(deleteRequest));
    }

    /**
     * 更新标签
     * @param tagUpdateRequest 更新标签请求体
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新标签", notes = "更新标签")
    public BaseResponse<Boolean> updateTag(@RequestBody TagUpdateRequest tagUpdateRequest) {
        return ResultUtils.success(tagService.updateTag(tagUpdateRequest));
    }

    /**
     * 获取标签（仅管理员）
     * @param id 标签 id
     * @return 标签
     */
    @GetMapping("/get")
    @ApiOperation(value = "获取标签", notes = "获取标签")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Tag> getTagById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Tag tag = tagService.getById(id);
        ThrowUtils.throwIf(tag == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(tag);
    }

    /**
     * 获取标签
     * @param id 标签 id
     * @return 标签
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "获取标签", notes = "获取标签")
    public BaseResponse<TagVO> getTagVOById(Long id) {
        return ResultUtils.success(tagService.getTagVOById(id));
    }

    /**
     * 分页获取标签列表（仅管理员）
     * @param tagQueryRequest 标签分页请求
     * @return 分页列表
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取标签", notes = "分页获取标签")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Tag>> listTagByPage(@RequestBody TagQueryRequest tagQueryRequest) {
        long current = tagQueryRequest.getCurrent();
        long size = tagQueryRequest.getPageSize();
        // 查询数据库
        Page<Tag> tagPage = tagService.page(new Page<>(current, size),
                tagService.getQueryWrapper(tagQueryRequest));
        return ResultUtils.success(tagPage);
    }

    /**
     * 分页获取标签列表
     * @param tagQueryRequest 标签分页请求
     * @return 分页列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取标签VO", notes = "分页获取标签VO")
    public BaseResponse<Page<TagVO>> listTagVOByPage(@RequestBody TagQueryRequest tagQueryRequest) {
        return ResultUtils.success(tagService.getTagVOPage(tagQueryRequest));
    }

    /**
     * 获取标签列表（用户创建图片使用）
     * @return 标签VO列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取标签列表", notes = "获取标签列表")
    public BaseResponse<List<TagVO>> listTagVO() {
        List<Tag> list = tagService.list();
        return ResultUtils.success(tagService.getTagVOList(list));
    }
}
