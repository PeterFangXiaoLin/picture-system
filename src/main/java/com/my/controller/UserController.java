package com.my.controller;

import com.my.common.BaseResponse;
import com.my.controller.vo.UserRegisterReqVO;
import com.my.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}
