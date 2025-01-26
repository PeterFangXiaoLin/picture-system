package com.my.aop;

import cn.hutool.core.util.ObjUtil;
import com.my.annotation.AuthCheck;
import com.my.common.ErrorCode;
import com.my.controller.vo.user.UserRespVO;
import com.my.domain.enums.UserRoleEnum;
import com.my.exception.BusinessException;
import com.my.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验
 * 通过 AOP + 自定义权限注解实现
 */
@Component
@Aspect
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 自定义权限注解
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 1. 获取当前登录用户
        // 获取request
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        UserRespVO loginUser = userService.getLoginUser(request);
        // 2. 获取 mustRole 属性
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限，放行
        if (ObjUtil.isNull(mustRoleEnum)) {
            return joinPoint.proceed();
        }
        // 获取当前登录用户所具备的权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (ObjUtil.isNull(userRoleEnum) || !userRoleEnum.equals(mustRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
