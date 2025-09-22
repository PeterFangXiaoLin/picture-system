package com.my.picturesystembackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.picturesystembackend.annotation.AuthCheck;
import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.constant.UserConstant;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.user.*;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.LoginUserVO;
import com.my.picturesystembackend.model.vo.UserVO;
import com.my.picturesystembackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Api(value = "user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求体
     * @return 新用户id
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public BaseResponse<Long> userRegister(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        return ResultUtils.success(userService.userRegister(userRegisterRequest));
    }

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求体
     * @param request 请求
     * @return 登录用户信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public BaseResponse<LoginUserVO> userLogin(@Valid @RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return ResultUtils.success(userService.userLogin(userLoginRequest, request));
    }

    /**
     * 获取当前登录的用户信息
     * @param request 请求
     * @return 登录用户信息
     */
    @GetMapping("/get/login")
    @ApiOperation(value = "获取当前登录的用户信息", notes = "获取当前登录的用户信息")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销（退出登录）
     * @param request 请求
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户注销（退出登录）", notes = "用户注销（退出登录）")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogout(request));
    }

    /**
     * 添加用户
     * @param userAddRequest 用户添加请求体
     * @return 新用户id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "添加用户", notes = "添加用户")
    public BaseResponse<Long> addUser(@Valid @RequestBody UserAddRequest userAddRequest) {
        return ResultUtils.success(userService.addUser(userAddRequest));
    }

    /**
     * 根据 id 获取用户（仅管理员）
     * @param id id
     * @return 用户
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "根据 id 获取用户（仅管理员）", notes = "根据 id 获取用户（仅管理员）")
    public BaseResponse<User> getUserById(@ApiParam(value = "id", type = "string") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取脱敏后的用户
     * @param id id
     * @return 脱敏后的用户
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取脱敏后的用户", notes = "根据 id 获取脱敏后的用户")
    public BaseResponse<UserVO> getUserVOById(@ApiParam(value = "id", type = "string") Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     * @param deleteRequest 删除请求体
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "删除用户", notes = "删除用户")
    public BaseResponse<Boolean> deleteUser(@Valid @RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(userService.deleteUser(deleteRequest));
    }

    /**
     * 更新用户
     * @param userUpdateRequest 用户更新请求体
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新用户", notes = "更新用户")
    public BaseResponse<Boolean> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResultUtils.success(userService.updateUser(userUpdateRequest));
    }

    /**
     * 分页获取用户列表（仅管理员）
     * @param userQueryRequest 用户查询请求体
     * @return 用户列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取用户列表（仅管理员）", notes = "分页获取用户列表（仅管理员）")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        return ResultUtils.success(userService.getUserVOByPage(userQueryRequest));
    }
}
