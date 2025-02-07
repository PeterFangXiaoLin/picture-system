package com.my.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.annotation.AuthCheck;
import com.my.common.BaseResponse;
import com.my.common.DeleteRequest;
import com.my.constant.UserConstant;
import com.my.controller.vo.user.*;
import com.my.domain.entity.User;
import com.my.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.my.common.ResultUtils.success;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterReqVO
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterReqVO userRegisterReqVO) {
        return success(userService.userRegister(userRegisterReqVO));
    }

    /**
     * 用户登录
     * @param userLoginReqVO
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserRespVO> userLogin(@RequestBody UserLoginReqVO userLoginReqVO, HttpServletRequest request) {
        return success(userService.userLogin(userLoginReqVO, request));
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/getLoginUser")
    public BaseResponse<UserRespVO> getLoginUser(HttpServletRequest request) {
        return success(userService.getLoginUser(request));
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return success(userService.userLogout(request));
    }

    /**
     * 重设密码
     *
     * @param userResetPasswordReqVO
     * @return
     */
    @PostMapping("/resetPassword")
    public BaseResponse<Boolean> resetPassword(@RequestBody UserResetPasswordReqVO userResetPasswordReqVO) {
        return success(userService.resetPassword(userResetPasswordReqVO));
    }

    /**
     * 根据id 获取用户 (仅管理员)
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(@RequestParam("id") Long id) {
        return success(userService.getUser(id));
    }

    /**
     * 根据id 获取用户信息
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserRespVO> getUserVOById(@RequestParam("id") Long id) {
        return success(userService.getUserVO(id));
    }

    /**
     * 创建用户
     * @param userAddReqVO
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddReqVO userAddReqVO) {
        return success(userService.addUser(userAddReqVO));
    }

    /**
     * 更新用户
     * @param userUpdateReqVO
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateReqVO userUpdateReqVO) {
        return success(userService.updateUser(userUpdateReqVO));
    }

    /**
     * 删除用户
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        return success(userService.deleteUser(deleteRequest));
    }

    @PostMapping("/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserRespVO>> pageUserVO(@RequestBody UserQueryReqVO userQueryReqVO) {
        return success(userService.pageUserVO(userQueryReqVO));
    }
}
