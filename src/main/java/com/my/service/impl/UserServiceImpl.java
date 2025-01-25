package com.my.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.common.ErrorCode;
import com.my.constant.UserConstant;
import com.my.controller.vo.UserRegisterReqVO;
import com.my.domain.entity.User;
import com.my.exception.BusinessException;
import com.my.service.UserService;
import com.my.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author helloworld
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-01-15 22:41:48
*/
@Service
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
}




