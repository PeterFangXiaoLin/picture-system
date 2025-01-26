package com.my.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.controller.vo.user.UserLoginReqVO;
import com.my.controller.vo.user.UserRegisterReqVO;
import com.my.controller.vo.user.UserRespVO;
import com.my.domain.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author helloworld
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-01-15 22:41:48
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterReqVO 用户注册请求体
     * @return 新用户 id
     */
    Long userRegister(UserRegisterReqVO userRegisterReqVO);


    /**
     * 密码加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String password);

    /**
     * 用户登录
     *
     * @param userLoginReqVO 用户登录请求体
     * @param request 请求
     * @return 用户响应体
     */
    UserRespVO userLogin(UserLoginReqVO userLoginReqVO, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 用户响应体
     */
    UserRespVO getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    boolean userLogout(HttpServletRequest request);
}
