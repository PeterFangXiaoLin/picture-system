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
import com.my.picturesystembackend.model.enums.SpaceRoleEnum;
import com.my.picturesystembackend.model.enums.SpaceUserInviteStatusEnum;
import com.my.picturesystembackend.model.vo.SpaceUserVO;
import com.my.picturesystembackend.model.vo.SpaceVO;
import com.my.picturesystembackend.model.vo.UserVO;
import com.my.picturesystembackend.service.SpaceService;
import com.my.picturesystembackend.service.SpaceUserService;
import com.my.picturesystembackend.mapper.SpaceUserMapper;
import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
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

    @Override
    public Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest, User loginUser) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NO_AUTH_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);

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
            return spaceUser.getId();
        }

        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    @Override
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

        // 带上旧状态作为更新条件，避免并发请求重复处理邀请
        boolean result = this.lambdaUpdate()
                .eq(SpaceUser::getId, id)
                .eq(SpaceUser::getUserId, loginUser.getId())
                .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.PENDING.getValue())
                .set(SpaceUser::getInviteStatus, inviteStatus)
                .update();
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "邀请已处理");
        return true;
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




