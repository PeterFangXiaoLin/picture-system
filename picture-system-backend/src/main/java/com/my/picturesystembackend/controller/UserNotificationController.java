package com.my.picturesystembackend.controller;

import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.entity.UserNotification;
import com.my.picturesystembackend.service.SpaceUserService;
import com.my.picturesystembackend.service.UserNotificationService;
import com.my.picturesystembackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户通知接口。
 *
 * <p>提供当前登录用户的通知列表、未读数量以及通知已读状态管理能力。</p>
 *
 * @author hello
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService notificationService;
    private final SpaceUserService spaceUserService;
    private final UserService userService;

    /**
     * 查询当前登录用户最近的通知。
     *
     * <p>通知按创建时间倒序排列，最多返回最近 100 条。</p>
     *
     * @param request HTTP 请求，用于获取当前登录用户
     * @return 当前登录用户的通知列表
     */
    @GetMapping("/list/my")
    @ApiOperation(value = "查询我的通知")
    public BaseResponse<List<UserNotification>> listMyNotification(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        spaceUserService.syncPendingInviteNotifications(loginUser);
        List<UserNotification> notifications = notificationService.lambdaQuery()
                .eq(UserNotification::getUserId, loginUser.getId())
                .orderByDesc(UserNotification::getCreateTime)
                .last("limit 100")
                .list();
        return ResultUtils.success(notifications);
    }

    /**
     * 查询当前登录用户的未读通知数量。
     *
     * @param request HTTP 请求，用于获取当前登录用户
     * @return 未读通知数量
     */
    @GetMapping("/unread/count")
    @ApiOperation(value = "查询我的未读通知数")
    public BaseResponse<Long> getUnreadCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        spaceUserService.syncPendingInviteNotifications(loginUser);
        long count = notificationService.lambdaQuery()
                .eq(UserNotification::getUserId, loginUser.getId())
                .eq(UserNotification::getIsRead, 0)
                .count();
        return ResultUtils.success(count);
    }

    /**
     * 将指定通知标记为已读。
     *
     * <p>只能操作属于当前登录用户的通知。</p>
     *
     * @param readRequest 包含通知 id 的请求参数
     * @param request HTTP 请求，用于获取当前登录用户
     * @return 是否标记成功
     */
    @PostMapping("/read")
    @ApiOperation(value = "标记通知为已读")
    public BaseResponse<Boolean> markRead(@RequestBody DeleteRequest readRequest,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(readRequest == null || readRequest.getId() == null || readRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean result = notificationService.lambdaUpdate()
                .eq(UserNotification::getId, readRequest.getId())
                .eq(UserNotification::getUserId, loginUser.getId())
                .set(UserNotification::getIsRead, 1)
                .update();
        ThrowUtils.throwIf(!result, ErrorCode.NOT_FOUND_ERROR, "通知不存在");
        return ResultUtils.success(true);
    }

    /**
     * 将当前登录用户的全部未读通知标记为已读。
     *
     * <p>该操作只改变阅读状态，不会改变空间邀请的待确认、已接受或已拒绝状态。</p>
     *
     * @param request HTTP 请求，用于获取当前登录用户
     * @return 是否处理成功
     */
    @PostMapping("/read/all")
    @ApiOperation(value = "全部通知标记为已读")
    public BaseResponse<Boolean> markAllRead(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        notificationService.lambdaUpdate()
                .eq(UserNotification::getUserId, loginUser.getId())
                .eq(UserNotification::getIsRead, 0)
                .set(UserNotification::getIsRead, 1)
                .update();
        return ResultUtils.success(true);
    }
}
