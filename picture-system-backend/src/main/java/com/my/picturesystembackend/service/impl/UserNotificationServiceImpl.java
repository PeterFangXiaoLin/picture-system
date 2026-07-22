package com.my.picturesystembackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.picturesystembackend.mapper.UserNotificationMapper;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.model.entity.UserNotification;
import com.my.picturesystembackend.service.UserNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserNotificationServiceImpl
        extends ServiceImpl<UserNotificationMapper, UserNotification>
        implements UserNotificationService {

    @Override
    public void sendNotification(Long userId, String type, String title, String content,
                                 Long relatedId, Long spaceId, Long actorUserId, Integer actionStatus) {
        // 统一通过构造方法填充通知字段，保证单发和批量发送的数据结构一致
        UserNotification notification = buildNotification(
                userId, type, title, content, relatedId, spaceId, actorUserId, actionStatus);
        if (!save(notification)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建通知失败");
        }
    }

    @Override
    public void sendNotification(Collection<Long> userIds, String type, String title, String content,
                                 Long relatedId, Long spaceId, Long actorUserId, Integer actionStatus) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        // 过滤无效用户并去重，避免同一业务事件向同一用户重复发送通知
        List<UserNotification> notifications = userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(userId -> buildNotification(
                        userId, type, title, content, relatedId, spaceId, actorUserId, actionStatus))
                .collect(Collectors.toList());
        if (!notifications.isEmpty()) {
            if (!saveBatch(notifications)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建通知失败");
            }
        }
    }

    @Override
    public void finishRelatedNotification(Long userId, String type, Long relatedId, Integer actionStatus) {
        // 只结束仍为待处理状态的通知；阅读状态与业务状态在一次更新中同步完成
        lambdaUpdate()
                .eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getType, type)
                .eq(UserNotification::getRelatedId, relatedId)
                .eq(UserNotification::getActionStatus, 0)
                .set(UserNotification::getIsRead, 1)
                .set(UserNotification::getActionStatus, actionStatus)
                .update();
    }

    private UserNotification buildNotification(Long userId, String type, String title, String content,
                                               Long relatedId, Long spaceId, Long actorUserId,
                                               Integer actionStatus) {
        // 新通知默认未读，actionStatus 用于区分邀请是否仍可接受或拒绝
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setSpaceId(spaceId);
        notification.setActorUserId(actorUserId);
        notification.setIsRead(0);
        notification.setActionStatus(actionStatus);
        return notification;
    }
}
