package com.my.picturesystembackend.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.my.picturesystembackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.SpaceUser;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.entity.UserNotification;
import com.my.picturesystembackend.model.enums.SpaceRoleEnum;
import com.my.picturesystembackend.model.enums.SpaceUserInviteStatusEnum;
import com.my.picturesystembackend.model.enums.UserNotificationTypeEnum;
import com.my.picturesystembackend.model.vo.SpaceUserVO;
import com.my.picturesystembackend.model.vo.SpaceVO;
import com.my.picturesystembackend.model.vo.UserVO;
import com.my.picturesystembackend.service.SpaceService;
import com.my.picturesystembackend.service.SpaceUserService;
import com.my.picturesystembackend.mapper.SpaceUserMapper;
import com.my.picturesystembackend.service.UserService;
import com.my.picturesystembackend.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author hello
* @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
* @createDate 2026-07-08 10:02:53
*/
@Service
@RequiredArgsConstructor
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserService{

    @Resource
    @Lazy
    private SpaceService spaceService;
    private final UserService userService;
    private final UserNotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest, User loginUser) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NO_AUTH_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);
        if (StrUtil.isBlank(spaceUser.getSpaceRole())) {
            spaceUser.setSpaceRole(SpaceRoleEnum.VIEWER.getValue());
        }

        // 只有已加入空间的管理员才可以发出邀请
        boolean isSpaceAdmin = this.lambdaQuery()
                .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                .eq(SpaceUser::getUserId, loginUser.getId())
                .eq(SpaceUser::getSpaceRole, SpaceRoleEnum.ADMIN.getValue())
                .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.ACCEPTED.getValue())
                .exists();
        ThrowUtils.throwIf(!isSpaceAdmin, ErrorCode.NO_AUTH_ERROR, "仅空间管理员可邀请成员");

        spaceUser.setCreateUserId(loginUser.getId());
        spaceUser.setInviteStatus(SpaceUserInviteStatusEnum.PENDING.getValue());

        // 同一个用户拒绝后允许管理员再次邀请；待确认或已接受时禁止重复邀请
        SpaceUser existingSpaceUser = this.lambdaQuery()
                .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                .eq(SpaceUser::getUserId, spaceUser.getUserId())
                .one();
        if (existingSpaceUser != null) {
            if (!Integer.valueOf(SpaceUserInviteStatusEnum.REJECTED.getValue())
                    .equals(existingSpaceUser.getInviteStatus())) {
                String message = Integer.valueOf(SpaceUserInviteStatusEnum.PENDING.getValue())
                        .equals(existingSpaceUser.getInviteStatus())
                        ? "邀请待用户确认，请勿重复邀请" : "用户已在空间中";
                throw new BusinessException(ErrorCode.OPERATION_ERROR, message);
            }
            spaceUser.setId(existingSpaceUser.getId());
            boolean result = this.updateById(spaceUser);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            sendInviteNotification(spaceUser, loginUser);
            return spaceUser.getId();
        }

        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        sendInviteNotification(spaceUser, loginUser);
        return spaceUser.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewSpaceUserInvite(Long id, Integer inviteStatus, User loginUser) {
        ThrowUtils.throwIf(id == null || id <= 0 || loginUser == null || loginUser.getId() == null,
                ErrorCode.PARAMS_ERROR);
        SpaceUserInviteStatusEnum statusEnum = SpaceUserInviteStatusEnum.getEnumByValue(inviteStatus);
        ThrowUtils.throwIf(statusEnum != SpaceUserInviteStatusEnum.ACCEPTED
                        && statusEnum != SpaceUserInviteStatusEnum.REJECTED,
                ErrorCode.PARAMS_ERROR, "只能接受或拒绝邀请");

        SpaceUser invitation = this.getById(id);
        ThrowUtils.throwIf(invitation == null, ErrorCode.NOT_FOUND_ERROR, "邀请不存在");
        ThrowUtils.throwIf(!loginUser.getId().equals(invitation.getUserId()),
                ErrorCode.NO_AUTH_ERROR, "只能处理发送给自己的邀请");
        ThrowUtils.throwIf(!Integer.valueOf(SpaceUserInviteStatusEnum.PENDING.getValue())
                        .equals(invitation.getInviteStatus()),
                ErrorCode.OPERATION_ERROR, "邀请已处理");

        // 在状态变更前获取现有管理员，避免新加入的管理员收到自己操作的结果通知
        List<Long> adminUserIds = this.lambdaQuery()
                .eq(SpaceUser::getSpaceId, invitation.getSpaceId())
                .eq(SpaceUser::getSpaceRole, SpaceRoleEnum.ADMIN.getValue())
                .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.ACCEPTED.getValue())
                .list()
                .stream()
                .map(SpaceUser::getUserId)
                .collect(Collectors.toList());

        // 带上旧状态作为更新条件，避免并发请求重复处理邀请
        boolean result = this.lambdaUpdate()
                .eq(SpaceUser::getId, id)
                .eq(SpaceUser::getUserId, loginUser.getId())
                .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.PENDING.getValue())
                .set(SpaceUser::getInviteStatus, inviteStatus)
                .update();
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "邀请已处理");

        notificationService.finishRelatedNotification(loginUser.getId(),
                UserNotificationTypeEnum.SPACE_INVITE.name(), invitation.getId(), inviteStatus);
        Space space = spaceService.getById(invitation.getSpaceId());
        String spaceName = space == null ? String.valueOf(invitation.getSpaceId()) : space.getSpaceName();
        String userName = getUserDisplayName(loginUser);
        boolean accepted = statusEnum == SpaceUserInviteStatusEnum.ACCEPTED;
        String type = accepted
                ? UserNotificationTypeEnum.SPACE_INVITE_ACCEPTED.name()
                : UserNotificationTypeEnum.SPACE_INVITE_REJECTED.name();
        String action = accepted ? "接受" : "拒绝";
        notificationService.sendNotification(adminUserIds, type,
                accepted ? "空间邀请已接受" : "空间邀请已拒绝",
                String.format("用户「%s」已%s加入空间「%s」的邀请", userName, action, spaceName),
                invitation.getId(), invitation.getSpaceId(), loginUser.getId(), inviteStatus);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncPendingInviteNotifications(User loginUser) {
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NO_AUTH_ERROR);

        // 锁定当前用户的待确认邀请，避免通知列表和未读数并发刷新时重复补数据
        List<SpaceUser> pendingInvitations = this.lambdaQuery()
                .eq(SpaceUser::getUserId, loginUser.getId())
                .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.PENDING.getValue())
                .last("FOR UPDATE")
                .list();
        if (CollectionUtils.isEmpty(pendingInvitations)) {
            return;
        }

        Set<Long> invitationIds = pendingInvitations.stream()
                .map(SpaceUser::getId)
                .collect(Collectors.toSet());
        Set<Long> notifiedInvitationIds = notificationService.lambdaQuery()
                .eq(UserNotification::getUserId, loginUser.getId())
                .eq(UserNotification::getType, UserNotificationTypeEnum.SPACE_INVITE.name())
                .eq(UserNotification::getActionStatus,
                        SpaceUserInviteStatusEnum.PENDING.getValue())
                .in(UserNotification::getRelatedId, invitationIds)
                .list()
                .stream()
                .map(UserNotification::getRelatedId)
                .collect(Collectors.toSet());

        // 旧邀请没有通知记录时按现有通知格式补齐，之后即可在通知中心接受或拒绝
        pendingInvitations.stream()
                .filter(invitation -> !notifiedInvitationIds.contains(invitation.getId()))
                .forEach(invitation -> {
                    User inviter = invitation.getCreateUserId() == null
                            ? null : userService.getById(invitation.getCreateUserId());
                    sendInviteNotification(invitation, inviter);
                });
    }

    private void sendInviteNotification(SpaceUser invitation, User inviter) {
        Space space = spaceService.getById(invitation.getSpaceId());
        String spaceName = space == null ? String.valueOf(invitation.getSpaceId()) : space.getSpaceName();
        SpaceRoleEnum roleEnum = SpaceRoleEnum.getEnumByValue(invitation.getSpaceRole());
        String roleName = roleEnum == null ? invitation.getSpaceRole() : roleEnum.getText();
        notificationService.sendNotification(invitation.getUserId(),
                UserNotificationTypeEnum.SPACE_INVITE.name(), "新的空间邀请",
                String.format("管理员「%s」邀请你加入空间「%s」，角色为%s",
                        getUserDisplayName(inviter), spaceName, roleName),
                invitation.getId(), invitation.getSpaceId(),
                inviter == null ? invitation.getCreateUserId() : inviter.getId(),
                SpaceUserInviteStatusEnum.PENDING.getValue());
    }

    private String getUserDisplayName(User user) {
        if (user == null) {
            return "未知用户";
        }
        return StrUtil.blankToDefault(user.getUserName(), user.getUserAccount());
    }

    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        // 创建时，空间id 和 用户id 必填
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            ThrowUtils.throwIf(ObjUtil.hasNull(spaceId, userId), ErrorCode.PARAMS_ERROR);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            User user = userService.getById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 校验空间角色
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间角色不存在");
        }
    }

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        Integer inviteStatus = spaceUserQueryRequest.getInviteStatus();
        Long createUserId = spaceUserQueryRequest.getCreateUserId();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotNull(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        queryWrapper.eq(StrUtil.isNotBlank(spaceRole), "spaceRole", spaceRole);
        queryWrapper.eq(ObjUtil.isNotNull(inviteStatus), "inviteStatus", inviteStatus);
        queryWrapper.eq(ObjUtil.isNotNull(createUserId), "createUserId", createUserId);
        return queryWrapper;
    }

    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request) {
        if (spaceUser == null) {
            return null;
        }
        // 对象转封装类
        SpaceUserVO spaceUserVO = SpaceUserVO.objToVo(spaceUser);
        // 关联查询用户信息
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceUserVO.setUser(userVO);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVO spaceVO = spaceService.getSpaceVO(space, request);
            spaceUserVO.setSpace(spaceVO);
        }
        return spaceUserVO;
    }

    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList) {
        if (CollectionUtils.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }

        // 对象列表 => 封装列表
        List<SpaceUserVO> spaceUserVOList = spaceUserList.stream()
                .map(SpaceUserVO::objToVo)
                .collect(Collectors.toList());

        // 收集关联查询用户id和空间id
        Set<Long> userIdSet = spaceUserList.stream()
                .map(SpaceUser::getUserId)
                .collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream()
                .map(SpaceUser::getSpaceId)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Space> spaceMap = spaceService.listByIds(spaceIdSet)
                .stream()
                .collect(Collectors.toMap(Space::getId, Function.identity()));

        // 填充 SpaceUserVO 的 空间和用户信息
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            User user = userMap.get(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceUserVO.setUser(userVO);

            Long spaceId = spaceUserVO.getSpaceId();
            Space space = spaceMap.get(spaceId);
            SpaceVO spaceVO = SpaceVO.objToVo(space);
            spaceUserVO.setSpace(spaceVO);
        });

        return spaceUserVOList;
    }
}




