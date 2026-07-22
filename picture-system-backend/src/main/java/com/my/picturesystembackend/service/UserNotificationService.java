package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.model.entity.UserNotification;

import java.util.Collection;

/**
 * 用户通知服务。
 *
 * <p>负责创建单用户或多用户通知，以及同步可操作通知的业务处理状态。</p>
 *
 * @author hello
 */
public interface UserNotificationService extends IService<UserNotification> {

    /**
     * 向单个用户发送通知。
     *
     * @param userId 接收用户 id
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param relatedId 关联业务 id，例如空间邀请对应的空间成员记录 id
     * @param spaceId 关联空间 id
     * @param actorUserId 触发通知的用户 id
     * @param actionStatus 可操作通知的业务状态，不可操作通知可传 {@code null}
     */
    void sendNotification(Long userId, String type, String title, String content,
                          Long relatedId, Long spaceId, Long actorUserId, Integer actionStatus);

    /**
     * 向多个用户批量发送相同内容的通知。
     *
     * <p>方法会过滤空用户 id，并对接收用户 id 去重。</p>
     *
     * @param userIds 接收用户 id 集合
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param relatedId 关联业务 id，例如空间邀请对应的空间成员记录 id
     * @param spaceId 关联空间 id
     * @param actorUserId 触发通知的用户 id
     * @param actionStatus 可操作通知的业务状态，不可操作通知可传 {@code null}
     */
    void sendNotification(Collection<Long> userIds, String type, String title, String content,
                          Long relatedId, Long spaceId, Long actorUserId, Integer actionStatus);

    /**
     * 完成指定业务通知，并同步更新阅读状态和业务处理状态。
     *
     * <p>只更新仍处于待处理状态的通知，避免覆盖已经结束的历史通知。</p>
     *
     * @param userId 通知接收用户 id
     * @param type 通知类型
     * @param relatedId 关联业务 id
     * @param actionStatus 完成后的业务状态
     */
    void finishRelatedNotification(Long userId, String type, Long relatedId, Integer actionStatus);
}
