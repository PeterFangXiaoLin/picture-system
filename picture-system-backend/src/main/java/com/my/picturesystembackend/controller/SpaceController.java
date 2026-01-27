package com.my.picturesystembackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.picturesystembackend.annotation.AuthCheck;
import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.constant.UserConstant;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.space.*;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.enums.SpaceLevelEnum;
import com.my.picturesystembackend.model.vo.SpaceVO;
import com.my.picturesystembackend.service.SpaceService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    /**
     * 添加空间
     *
     * @param spaceAddRequest 添加空间请求体
     * @param request         request
     * @return 新空间 id
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加空间")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        return ResultUtils.success(spaceService.addSpace(spaceAddRequest, request));
    }

    /**
     * 删除空间
     *
     * @param deleteRequest 删除请求体
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除空间")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(spaceService.deleteSpace(deleteRequest, request));
    }

    /**
     * 更新空间信息
     *
     * @param spaceUpdateRequest 更新空间请求体
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新空间信息")
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest, HttpServletRequest request) {
        return ResultUtils.success(spaceService.updateSpace(spaceUpdateRequest));
    }

    /**
     * 根据 id 获取空间（仅管理员可用）
     *
     * @param id      空间 id
     * @param request request
     * @return 空间
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "根据id获取空间（仅管理员可用）")
    public BaseResponse<Space> getSpaceById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0L, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(space);
    }

    /**
     * 根据 id 获取空间（封装类）
     *
     * @param id      空间 id
     * @param request request
     * @return 空间封装类
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取空间（封装类）")
    public BaseResponse<SpaceVO> getSpaceVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0L, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
        return ResultUtils.success(spaceVO);
    }

    /**
     * 分页获取空间列表（仅管理员）
     *
     * @param spaceQueryRequest 空间查询请求体
     * @param request           request
     * @return 空间分页
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取空间列表（仅管理员）")
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = spaceQueryRequest.getCurrent();
        long pageSize = spaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, pageSize),
                spaceService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spacePage);
    }

    /**
     * 分页获取空间列表（封装类）
     *
     * @param spaceQueryRequest 空间查询请求体
     * @param request           request
     * @return 空间封装类分页
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取空间列表（封装类）")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = spaceQueryRequest.getCurrent();
        long pageSize = spaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, pageSize),
                spaceService.getQueryWrapper(spaceQueryRequest));
        // 转换为封装类分页
        Page<SpaceVO> spaceVOPage = spaceService.getSpaceVOPage(spacePage, request);
        return ResultUtils.success(spaceVOPage);
    }

    /**
     * 编辑空间（用户）
     *
     * @param spaceEditRequest 编辑空间请求体
     * @param request          request
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑空间（用户）")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        return ResultUtils.success(spaceService.editSpace(spaceEditRequest, request));
    }

    /**
     * 获取空间级别
     *
     * @return 空间级别列表
     */
    @GetMapping("/list/level")
    @ApiOperation(value = "获取空间级别")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }
}
