package com.my.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.common.DeleteRequest;
import com.my.controller.vo.user.*;
import com.my.domain.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 创建用户
     *
     * @param userAddReqVO 用户添加请求体
     * @return 用户 id
     */
    Long addUser(UserAddReqVO userAddReqVO);

    /**
     * 更新用户
     *
     * @param userUpdateReqVO
     * @return
     */
    Boolean updateUser(UserUpdateReqVO userUpdateReqVO);

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @return
     */
    Boolean deleteUser(DeleteRequest deleteRequest);

    /**
     * 校验用户
     *
     * @param userId
     */
    void validateUser(Long userId);

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @return
     */
    User getUser(Long id);

    /**
     * 根据 id 获取用户（脱敏）
     *
     * @param id
     * @return
     */
    UserRespVO getUserVO(Long id);

    /**
     * 分页查询用户（脱敏）
     *
     * @param reqVO
     * @return
     */
    Page<UserRespVO> pageUserVO(UserQueryReqVO reqVO);

    /**
     * userList to userVOList
     *
     * @param userList
     * @return
     */
    List<UserRespVO> getUserVOList(List<User> userList);
}
