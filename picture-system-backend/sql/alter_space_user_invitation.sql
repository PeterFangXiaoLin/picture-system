-- 已有数据库升级脚本：历史成员视为已接受，邀请人暂按成员本人回填。
ALTER TABLE space_user
    ADD COLUMN inviteStatus TINYINT DEFAULT 1 NOT NULL COMMENT '邀请确认状态：0-待确认，1-已接受，2-已拒绝' AFTER spaceRole,
    ADD COLUMN createUserId BIGINT NULL COMMENT '邀请人 id' AFTER inviteStatus;

UPDATE space_user
SET createUserId = userId
WHERE createUserId IS NULL;

ALTER TABLE space_user
    MODIFY COLUMN inviteStatus TINYINT DEFAULT 0 NOT NULL COMMENT '邀请确认状态：0-待确认，1-已接受，2-已拒绝',
    MODIFY COLUMN createUserId BIGINT NOT NULL COMMENT '邀请人 id',
    ADD INDEX idx_createUserId (createUserId);
