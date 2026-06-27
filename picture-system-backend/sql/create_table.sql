create database if not exists `picture_system`;

use `picture_system`;

-- 用户表
create table if not exists user
(
    id           bigint comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    INDEX idx_userAccount (userAccount),
    INDEX idx_userName (userName),
    INDEX idx_createTime (createTime)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 分类表
create table if not exists category
(
    id         bigint comment 'id' primary key,
    name       varchar(128)                       not null comment '分类名称',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    INDEX idx_name (name),                   -- 提升基于分类名称的查询性能
    INDEX idx_userId (userId),               -- 提升基于用户 ID 的查询性能
    INDEX idx_createTime (createTime),       -- 提升基于创建时间的查询性能
    UNIQUE KEY uk_name_userId (name, userId) -- 同一用户下分类名称唯一
) comment '图片分类' collate = utf8mb4_unicode_ci;

-- 标签表
create table if not exists tag
(
    id         bigint comment 'id' primary key,
    name       varchar(128)                       not null comment '标签名称',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    INDEX idx_name (name),                   -- 提升基于标签名称的查询性能
    INDEX idx_userId (userId),               -- 提升基于用户 ID 的查询性能
    INDEX idx_createTime (createTime),       -- 提升基于创建时间的查询性能
    UNIQUE KEY uk_name_userId (name, userId) -- 同一用户下标签名称唯一
) comment '图片标签' collate = utf8mb4_unicode_ci;

-- 图片表
create table if not exists picture
(
    id           bigint comment 'id' primary key,
    url          varchar(512)                       not null comment '图片 url',
    name         varchar(128)                       not null comment '图片名称',
    introduction varchar(512)                       null comment '简介',
    categoryId   bigint                             null comment '分类 id',
    picSize      bigint                             null comment '图片体积',
    picWidth     int                                null comment '图片宽度',
    picHeight    int                                null comment '图片高度',
    picScale     double                             null comment '图片宽高比例',
    picFormat    varchar(32)                        null comment '图片格式',
    userId       bigint                             not null comment '创建用户 id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    INDEX idx_name (name),                 -- 提升基于图片名称的查询性能
    INDEX idx_introduction (introduction), -- 用于模糊搜索图片简介
    INDEX idx_categoryId (categoryId),     -- 提升基于分类的查询性能
    INDEX idx_userId (userId),             -- 提升基于用户 ID 的查询性能
    INDEX idx_createTime (createTime)      -- 提升基于创建时间的查询性能
) comment '图片' collate = utf8mb4_unicode_ci;

ALTER TABLE picture
    ADD COLUMN tags varchar(512) null comment '标签ids(逗号分隔)' AFTER categoryId;

ALTER TABLE picture
    -- 添加新列
    ADD COLUMN reviewStatus  INT DEFAULT 0 NOT NULL COMMENT '审核状态：0-待审核; 1-通过; 2-拒绝',
    ADD COLUMN reviewMessage VARCHAR(512)  NULL COMMENT '审核信息',
    ADD COLUMN reviewerId    BIGINT        NULL COMMENT '审核人 ID',
    ADD COLUMN reviewTime    DATETIME      NULL COMMENT '审核时间';

-- 创建基于 reviewStatus 列的索引
CREATE INDEX idx_reviewStatus ON picture (reviewStatus);

-- 增加压缩图url列
ALTER TABLE picture
    -- 添加新列
    ADD COLUMN compressedUrl varchar(512) NULL COMMENT '压缩图 url';

-- 增加点赞数量列
ALTER TABLE picture
    -- 添加新列
    ADD COLUMN thumbCount INT DEFAULT 0 NOT NULL COMMENT '点赞数量';

-- 点赞记录表
create table if not exists thumb
(
    id         bigint primary key comment 'id',
    userId     bigint                             not null comment '用户id',
    pictureId  bigint                             not null comment '图片id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间'
) comment '点赞记录表' collate = utf8mb4_unicode_ci;
create unique index idx_userId_blogId on thumb (userId, pictureId);

-- 空间表
create table if not exists space
(
    id         bigint auto_increment comment 'id' primary key,
    spaceName  varchar(128)                       null comment '空间名称',
    spaceLevel int      default 0                 null comment '空间级别：0-普通版 1-专业版 2-旗舰版',
    maxSize    bigint   default 0                 null comment '空间图片的最大总大小',
    maxCount   bigint   default 0                 null comment '空间图片的最大数量',
    totalSize  bigint   default 0                 null comment '当前空间下图片的总大小',
    totalCount bigint   default 0                 null comment '当前空间下的图片数量',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    -- 索引设计
    index idx_userId (userId),        -- 提升基于用户的查询效率
    index idx_spaceName (spaceName),  -- 提升基于空间名称的查询效率
    index idx_spaceLevel (spaceLevel) -- 提升按空间级别查询的效率
) comment '空间' collate = utf8mb4_unicode_ci;

-- 添加新列
ALTER TABLE picture
    ADD COLUMN spaceId bigint null comment '空间 id（为空表示公共空间）';

-- 创建索引
CREATE INDEX idx_spaceId ON picture (spaceId);

-- 添加新列
ALTER TABLE picture
    ADD COLUMN picColor varchar(16) null comment '图片主色调';

-- AI 扩图任务记录表
create table if not exists out_painting_task
(
    id               bigint comment 'id' primary key,
    taskId           varchar(128)                       not null comment '阿里云任务 id',
    pictureId        bigint                             not null comment '原图片 id',
    userId           bigint                             not null comment '创建用户 id',
    taskStatus       varchar(32)  default 'PENDING'     not null comment '任务状态',
    parameters       text                               null comment '扩图参数 JSON',
    originalImageUrl varchar(512)                       null comment '原图 url',
    outputImageUrl   varchar(512)                       null comment '输出图像 url',
    errorCode        varchar(128)                       null comment '错误码',
    errorMessage     varchar(512)                       null comment '错误信息',
    parentTaskId     bigint                             null comment '重试时关联的原任务记录 id',
    createTime       datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint      default 0             not null comment '是否删除',
    INDEX idx_userId (userId),
    INDEX idx_pictureId (pictureId),
    INDEX idx_taskId (taskId),
    INDEX idx_taskStatus (taskStatus),
    INDEX idx_createTime (createTime)
) comment 'AI 扩图任务记录' collate = utf8mb4_unicode_ci;

-- 用户表增加 AI 扩图次数
ALTER TABLE user
    ADD COLUMN outPaintingQuota INT DEFAULT 8 NOT NULL COMMENT 'AI 扩图剩余次数' AFTER userRole;

-- 扩图任务表增加次数退还标记
ALTER TABLE out_painting_task
    ADD COLUMN quotaRefunded tinyint DEFAULT 0 NOT NULL COMMENT '是否已退还扩图次数' AFTER parentTaskId;

