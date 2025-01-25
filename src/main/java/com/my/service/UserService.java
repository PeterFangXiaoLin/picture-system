package com.my.service;

import com.my.controller.vo.UserRegisterReqVO;
import com.my.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
