package com.my.controller;

import com.my.common.BaseResponse;
import com.my.controller.vo.user.UserLoginReqVO;
import com.my.controller.vo.user.UserRegisterReqVO;
import com.my.controller.vo.user.UserRespVO;
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

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterReqVO userRegisterReqVO) {
        return success(userService.userRegister(userRegisterReqVO));
    }

    @PostMapping("/login")
    public BaseResponse<UserRespVO> userLogin(@RequestBody UserLoginReqVO userLoginReqVO, HttpServletRequest request) {
        return success(userService.userLogin(userLoginReqVO, request));
    }

    @GetMapping("/getLoginUser")
    public BaseResponse<UserRespVO> getLoginUser(HttpServletRequest request) {
        return success(userService.getLoginUser(request));
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return success(userService.userLogout(request));
    }
}
