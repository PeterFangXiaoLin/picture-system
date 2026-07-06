package com.my.picturesystembackend.controller;

import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.space.analyze.*;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.space.analyze.*;
import com.my.picturesystembackend.service.SpaceAnalyzeService;
import com.my.picturesystembackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 空间分析控制器
 */
@RequestMapping("/space/analyze")
@RestController
@RequiredArgsConstructor
public class SpaceAnalyzeController {

    private final SpaceAnalyzeService spaceAnalyzeService;
    private final UserService userService;

    /**
     * 获取空间使用情况
     * @param spaceUsageAnalyzeRequest 空间使用情况请求
     * @param request request
     * @return 空间使用情况响应
     */
    @PostMapping("/usage")
    @ApiOperation("获取空间使用情况")
    public BaseResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(@RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
                                                                        HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(spaceAnalyzeService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginUser));
    }

    /**
     * 获取空间分类情况
     *
     * @param spaceCategoryAnalyzeRequest 空间分类情况请求
     * @param request request
     * @return 空间分类情况响应
     */
    @PostMapping("/category")
    @ApiOperation("获取空间分类情况")
    public BaseResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(@RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest,
                                                                                    HttpServletRequest request) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(spaceAnalyzeService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginUser));
    }

    /**
     * 获取空间标签情况分析
     *
     * @param spaceTagAnalyzeRequest 空间标签分析请求
     * @param request request
     * @return 空间标签分析响应
     */
    @PostMapping("/tag")
    @ApiOperation("获取空间标签情况")
    public BaseResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(@RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest,
                                                                          HttpServletRequest request) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(spaceAnalyzeService.getSpaceTagAnalyze(spaceTagAnalyzeRequest, loginUser));
    }

    /**
     * 获取空间图片大小统计
     *
     * @param spaceSizeAnalyzeRequest 空间图片大小统计
     * @param request request
     * @return 空间图片大小统计响应
     */
    @PostMapping("/size")
    @ApiOperation("获取空间图片大小统计")
    public BaseResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(@RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest,
                                                                            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(spaceAnalyzeService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginUser));
    }

    /**
     * 获取空间用户上传情况
     *
     * @param spaceUserAnalyzeRequest 空间用户上传分析请求
     * @param request request
     * @return 空间用户上传分析响应
     */
    @PostMapping("/user")
    @ApiOperation("获取空间用户上传情况")
    public BaseResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(@RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest,
                                                                            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(spaceAnalyzeService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginUser));
    }

    /**
     * 获取空间使用Top N
     *
     * @param spaceRankAnalyzeRequest 空间使用排行请求
     * @param request request
     * @return 空间使用排行响应
     */
    @PostMapping("/rank")
    @ApiOperation("获取空间使用TOP N")
    public BaseResponse<List<Space>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest,
                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(spaceAnalyzeService.getSpaceRankAnalyze(spaceRankAnalyzeRequest, loginUser));
    }
}
