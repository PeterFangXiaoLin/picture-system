package com.my.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.common.DeleteRequest;
import com.my.common.ErrorCode;
import com.my.constant.CommonConstant;
import com.my.constant.UserConstant;
import com.my.controller.vo.user.*;
import com.my.domain.entity.User;
import com.my.exception.BusinessException;
import com.my.exception.ThrowUtils;
import com.my.mapper.UserMapper;
import com.my.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.my.constant.UserConstant.*;

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
        if (userAccount.length() < minUserAccountLen || userAccount.length() > maxUserAccountLen) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度在 4-16 位");
        }
        if (userPassword.length() < minPasswordLen || userPassword.length() > maxPasswordLen) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度在 8-20 位");
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
        UserRespVO userRespVO = BeanUtil.copyProperties(user, UserRespVO.class);

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
        return BeanUtil.copyProperties(user, UserRespVO.class);
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

    @Override
    public Long addUser(UserAddReqVO userAddReqVO) {
        ThrowUtils.throwIf(ObjUtil.isNull(userAddReqVO), ErrorCode.PARAMS_ERROR);
        // 校验账号是否重复
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, userAddReqVO.getUserAccount()));
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号已存在");
        User user = BeanUtil.copyProperties(userAddReqVO, User.class);
        // 设置默认密码
        user.setUserPassword(getEncryptPassword(UserConstant.DEFAULT_PASSWORD));
        // 用户名为空时，设置默认用户名
        if (StrUtil.isBlank(user.getUserName())) {
            user.setUserName(UserConstant.DEFAULT_USER_NAME);
        }
        int insert = userMapper.insert(user);
        ThrowUtils.throwIf(insert < 0, ErrorCode.SYSTEM_ERROR, "添加用户失败");
        return user.getId();
    }

    @Override
    public Boolean updateUser(UserUpdateReqVO userUpdateReqVO) {
        ThrowUtils.throwIf(ObjUtil.isNull(userUpdateReqVO) || ObjUtil.isNull(userUpdateReqVO.getId()), ErrorCode.PARAMS_ERROR);
        // 校验是否存在
        validateUser(userUpdateReqVO.getId());
        User user = BeanUtil.copyProperties(userUpdateReqVO, User.class);
        int update = userMapper.updateById(user);
        ThrowUtils.throwIf(update < 0, ErrorCode.SYSTEM_ERROR, "更新用户失败");
        return true;
    }

    @Override
    public Boolean deleteUser(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(ObjUtil.isNull(deleteRequest) || ObjUtil.isNull(deleteRequest.getId()), ErrorCode.PARAMS_ERROR);
        // 校验是否存在
        validateUser(deleteRequest.getId());
        int delete = userMapper.deleteById(deleteRequest.getId());
        ThrowUtils.throwIf(delete < 0, ErrorCode.SYSTEM_ERROR, "删除用户失败");
        return true;
    }

    @Override
    public void validateUser(Long userId) {
        ThrowUtils.throwIf(ObjUtil.isNull(userId), ErrorCode.PARAMS_ERROR, "用户不存在");
        User user = userMapper.selectById(userId);
        ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.PARAMS_ERROR, "用户不存在");
    }

    @Override
    public User getUser(Long id) {
        ThrowUtils.throwIf(ObjUtil.isNull(id) || id <= 0, ErrorCode.PARAMS_ERROR, "用户不存在");
        User user = userMapper.selectById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.PARAMS_ERROR, "用户不存在");
        return user;
    }

    @Override
    public UserRespVO getUserVO(Long id) {
        ThrowUtils.throwIf(ObjUtil.isNull(id) || id <= 0, ErrorCode.PARAMS_ERROR, "用户不存在");
        User user = userMapper.selectById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.PARAMS_ERROR, "用户不存在");
        return BeanUtil.copyProperties(user, UserRespVO.class);
    }

    @Override
    public Page<UserRespVO> pageUserVO(UserQueryReqVO reqVO) {
        ThrowUtils.throwIf(ObjUtil.isNull(reqVO), ErrorCode.PARAMS_ERROR);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(reqVO.getId()), "id", reqVO.getId());
        queryWrapper.eq(StrUtil.isNotBlank(reqVO.getUserRole()), "userRole", reqVO.getUserRole());
        queryWrapper.like(StrUtil.isNotBlank(reqVO.getUserName()), "userName", reqVO.getUserName());
        queryWrapper.like(StrUtil.isNotBlank(reqVO.getUserAccount()), "userAccount", reqVO.getUserAccount());
        queryWrapper.like(StrUtil.isNotBlank(reqVO.getUserProfile()), "userProfile", reqVO.getUserProfile());
        queryWrapper.orderBy(StrUtil.isNotEmpty(reqVO.getSortField()), reqVO.getSortOrder().equals(CommonConstant.SORT_ORDER_ASC), reqVO.getSortField());

        Page<User> userPage = userMapper.selectPage(Page.of(reqVO.getCurrent(), reqVO.getPageSize()), queryWrapper);
        Page<UserRespVO> userRespVOPage = new Page<>(reqVO.getCurrent(), reqVO.getPageSize(), userPage.getTotal());
        userRespVOPage.setRecords(getUserVOList(userPage.getRecords()));
        return userRespVOPage;
    }

    @Override
    public List<UserRespVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return CollUtil.newArrayList();
        }
        return userList.stream().map(user -> BeanUtil.copyProperties(user, UserRespVO.class)).collect(Collectors.toList());
    }

    @Override
    public Boolean resetPassword(UserResetPasswordReqVO userResetPasswordReqVO, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isNull(userResetPasswordReqVO) || ObjUtil.isNull(userResetPasswordReqVO.getId()), ErrorCode.PARAMS_ERROR);
        // 校验是否存在
        validateUser(userResetPasswordReqVO.getId());
        String oldPassword = userResetPasswordReqVO.getOldPassword();
        ThrowUtils.throwIf(StrUtil.isBlank(oldPassword) || oldPassword.length() < minPasswordLen || oldPassword.length() > maxPasswordLen, ErrorCode.PARAMS_ERROR, "旧密码错误");
        String newPassword = userResetPasswordReqVO.getNewPassword();
        ThrowUtils.throwIf(StrUtil.isBlank(newPassword) || newPassword.length() < minPasswordLen || newPassword.length() > maxPasswordLen, ErrorCode.PARAMS_ERROR, "密码长度在 8-20 位");
        User user = userMapper.selectById(userResetPasswordReqVO.getId());
        ThrowUtils.throwIf(!getEncryptPassword(oldPassword).equals(user.getUserPassword()), ErrorCode.PARAMS_ERROR, "旧密码错误");
        String encryptNewPassword = getEncryptPassword(newPassword);
        ThrowUtils.throwIf(encryptNewPassword.equals(user.getUserPassword()), ErrorCode.PARAMS_ERROR, "新密码不能与旧密码相同");
        user.setUserPassword(encryptNewPassword);
        int update = userMapper.updateById(user);
        ThrowUtils.throwIf(update < 0, ErrorCode.SYSTEM_ERROR, "更新密码失败");
        // 移除用户的登录态(默认认为用户已登录)
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public Boolean editUserInfo(UserEditReqVO userEditReqVO) {
        ThrowUtils.throwIf(ObjUtil.isNull(userEditReqVO) || ObjUtil.isNull(userEditReqVO.getId()), ErrorCode.PARAMS_ERROR);
        // 校验是否存在
        validateUser(userEditReqVO.getId());
        User user = BeanUtil.copyProperties(userEditReqVO, User.class);
        int update = userMapper.updateById(user);
        ThrowUtils.throwIf(update < 0, ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
        return true;
    }
}




