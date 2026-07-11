package com.my.picturesystembackend.controller;

import cn.hutool.core.util.ObjUtil;
import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.my.picturesystembackend.model.dto.spaceuser.SpaceUserEditRequest;
import com.my.picturesystembackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.my.picturesystembackend.model.entity.SpaceUser;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.SpaceUserVO;
import com.my.picturesystembackend.service.SpaceUserService;
import com.my.picturesystembackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/spaceUser")
@RequiredArgsConstructor
public class SpaceUserController {

    private final SpaceUserService spaceUserService;
    private final UserService userService;

    /**
     * 添加成员到空间
     *
     * @param spaceUserAddRequest 空间成员添加请求
     * @param request             request
     * @return 添加成功id
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加成员到空间")
    public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest,
                                           HttpServletRequest request) {
        return ResultUtils.success(spaceUserService.addSpaceUser(spaceUserAddRequest));
    }

    /**
     * 删除空间成员
     *
     * @param deleteRequest 删除请求
     * @param request       request
     * @return 删除结果
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除空间成员")
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest,
                                                 HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR, "用户不是空间成员");
        boolean result = spaceUserService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询某个成员在某个空间的信息
     *
     * @param spaceUserQueryRequest 空间成员查询请求
     * @return 某个空间的某个成员信息
     */
    @PostMapping("/get")
    @ApiOperation(value = "查询某个成员在某个空间的信息")
    public BaseResponse<SpaceUser> getSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest) {
        ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = spaceUserQueryRequest.getUserId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        ThrowUtils.throwIf(ObjUtil.hasNull(userId, spaceId), ErrorCode.PARAMS_ERROR);
        // 查询数据库
        SpaceUser spaceUser = spaceUserService.lambdaQuery()
                .eq(SpaceUser::getUserId, userId)
                .eq(SpaceUser::getSpaceId, spaceId)
                .one();
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(spaceUser);
    }

    /**
     * 查询成员信息列表
     *
     * @param spaceUserQueryRequest 空间查询请求体
     * @param request               request
     * @return 空间成员信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询成员信息列表")
    public BaseResponse<List<SpaceUserVO>> listSpaceUser(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(spaceUserQueryRequest)
        );
        return ResultUtils.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }

    /**
     * 编辑成员信息 （设置权限）
     *
     * @param spaceUserEditRequest 空间成员编辑请请求
     * @param request              request
     * @return 编辑结果
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑成员信息（设置权限）")
    public BaseResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest,
                                               HttpServletRequest request) {
        if (spaceUserEditRequest == null || spaceUserEditRequest.getId() == null
                || spaceUserEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 只有已经加入空间的成员才能编辑
        Long id = spaceUserEditRequest.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        ThrowUtils.throwIf(oldSpaceUser == null, ErrorCode.NOT_FOUND_ERROR, "用户不是空间成员");
        // 校验参数
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserEditRequest, spaceUser);
        spaceUserService.validSpaceUser(spaceUser, false);
        // 角色没有变化时无需更新数据库
        if (Objects.equals(oldSpaceUser.getSpaceRole(), spaceUserEditRequest.getSpaceRole())) {
            return ResultUtils.success(true);
        }
        // 操作数据库
        boolean result = spaceUserService.updateById(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查询我加入的团队空间列表
     *
     * @param request request
     * @return 团队空间列表
     */
    @PostMapping("/list/my")
    @ApiOperation(value = "查询我加入的团队空间列表")
    public BaseResponse<List<SpaceUserVO>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<SpaceUser> spaceUserList = spaceUserService.lambdaQuery()
                .eq(SpaceUser::getUserId, loginUser.getId())
                .list();
        return ResultUtils.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }
}
