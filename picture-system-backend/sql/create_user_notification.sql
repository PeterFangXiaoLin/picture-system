create table if not exists user_notification
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                              not null comment '接收用户 id',
    type        varchar(64)                         not null comment '通知类型',
    title       varchar(128)                        not null comment '通知标题',
    content     varchar(512)                        not null comment '通知内容',
    relatedId   bigint                              null comment '关联业务 id',
    spaceId     bigint                              null comment '空间 id',
    actorUserId bigint                              null comment '触发通知的用户 id',
    isRead      tinyint  default 0                  not null comment '是否已读：0-未读，1-已读',
    actionStatus tinyint                            null comment '可操作通知业务状态：0-待确认，1-已接受，2-已拒绝',
    createTime  datetime default CURRENT_TIMESTAMP  not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP  not null on update CURRENT_TIMESTAMP comment '更新时间',
    INDEX idx_userId_isRead_createTime (userId, isRead, createTime),
    INDEX idx_relatedId (relatedId),
    INDEX idx_spaceId (spaceId)
) comment '用户通知' collate = utf8mb4_unicode_ci;
