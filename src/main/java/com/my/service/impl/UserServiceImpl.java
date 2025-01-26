package com.my.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.common.ErrorCode;
import com.my.constant.UserConstant;
import com.my.controller.vo.user.UserLoginReqVO;
import com.my.controller.vo.user.UserRegisterReqVO;
import com.my.controller.vo.user.UserRespVO;
import com.my.convert.UserConvert;
import com.my.domain.entity.User;
import com.my.exception.BusinessException;
import com.my.exception.ThrowUtils;
import com.my.mapper.UserMapper;
import com.my.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.my.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author helloworld
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-01-15 22:41:48
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public Long userRegister(UserRegisterReqVO userRegisterReqVO) {
        // 校验
        String userAccount = userRegisterReqVO.getUserAccount();
        String userPassword = userRegisterReqVO.getUserPassword();
        String checkPassword = userRegisterReqVO.getCheckPassword();
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度在 4-16 位");
        }
        if (userPassword.length() < 8 || userPassword.length() > 16 || checkPassword.length() < 8 || checkPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度在 8-16 位");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        // 账户不能重复
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUserAccount, userAccount));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 加密
        String encryptPassword = getEncryptPassword(userPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(UserConstant.DEFAULT_USER_NAME); // 默认用户名
        int insert = userMapper.insert(user);
        if (insert < 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败, 系统错误");
        }
        return user.getId();
    }

    @Override
    public String getEncryptPassword(String password) {
        // 盐值
        final String SALT = "my";
        // md5加密
        return DigestUtil.md5Hex(SALT + password);
    }

    @Override
    public UserRespVO userLogin(UserLoginReqVO userLoginReqVO, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isNull(userLoginReqVO), ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginReqVO.getUserAccount();
        String userPassword = userLoginReqVO.getUserPassword();
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度在 4-16 位");
        }
        if (userPassword.length() < 8 || userPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度在 8-16 位");
        }
        // 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 查询用户是否存在
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, encryptPassword)
                .last("limit 1"));

        if (ObjUtil.isNull(user)) {
            log.info("User login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在或密码错误");
        }
        // 脱敏用户信息
        UserRespVO userRespVO = UserConvert.INSTANCE.userDOToUserRespVO(user);

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userRespVO);
        // 返回脱敏后的信息
        return userRespVO;
    }

    @Override
    public UserRespVO getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        UserRespVO userRespVO = (UserRespVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        // 校验是否存在
        if (ObjUtil.isNull(userRespVO) || ObjUtil.isNull(userRespVO.getId())) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 向数据库查询（追求性能的话可以注释，直接走缓存）
        User user = userMapper.selectById(userRespVO.getId());
        if (ObjUtil.isNull(user)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 脱敏用户信息
        return UserConvert.INSTANCE.userDOToUserRespVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjUtil.isNull(userObj), ErrorCode.OPERATION_ERROR, "未登录");
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

}




