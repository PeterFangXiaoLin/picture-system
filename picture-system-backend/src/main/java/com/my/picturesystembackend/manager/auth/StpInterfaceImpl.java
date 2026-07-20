package com.my.picturesystembackend.manager.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.my.picturesystembackend.constant.UserConstant;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.manager.auth.model.SpaceUserPermissionConstant;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.SpaceUser;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.enums.SpaceRoleEnum;
import com.my.picturesystembackend.model.enums.SpaceTypeEnum;
import com.my.picturesystembackend.model.enums.SpaceUserInviteStatusEnum;
import com.my.picturesystembackend.service.PictureService;
import com.my.picturesystembackend.service.SpaceService;
import com.my.picturesystembackend.service.SpaceUserService;
import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SpaceUserAuthManager spaceUserAuthManager;
    private final SpaceUserService spaceUserService;
    private final PictureService pictureService;
    private final UserService userService;
    private final SpaceService spaceService;

    /**
     * 返回一个账号所拥有的权限码集合 (**本项目使用**)
     *
     * 核心：从 authContext 获取的内容都是被操作的对象
     *
     * 1. 校验 loginType 是否是 space
     * 2. 如果所有操作字段为空，放行返回管理员权限（不在需要校验权限的范围内）
     *
     * 3. 获取请求的上下文对象
     *     a. 判断 SpaceUser 是否为空，不为空直接返回 该角色所具有的permissions
     *     b. 判断 spaceUserId 是否为空，查询该 id对应的 SpaceUser, 判断是否存在
     *          判断当前登录用户是否在该空间中，不在就报错没权限
     *          在空间中，获取其角色，拿到权限列表返回
     *     c. 上面都为空，拿到空间id,如果空间id也为空，拿到picture id，如果picture id
     *          也为空，那也就是全空，返回管理员，表示操作的业务不在需要鉴权的范围内
     *
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 判断 loginType，仅对 "space" 进行权限校验
        if (!StpKit.SPACE_TYPE.equals(loginType)) {
            return Collections.emptyList();
        }
        List<String> adminPermissions = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        SpaceUserAuthContext authContext = getAuthContextByRequest();
        // 编程式鉴权必须明确传入被操作对象；缺少上下文时失败关闭，避免误放行。
        if (authContext == null) {
            return Collections.emptyList();
        }
        // 获取当前登录用户id
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        Long userId = loginUser.getId();
        // spaceUser / spaceUserId 表示被操作的成员，而不是当前操作者。
        SpaceUser targetSpaceUser = authContext.getSpaceUser();
        Long spaceUserId = authContext.getSpaceUserId();
        if (targetSpaceUser != null || spaceUserId != null) {
            if (targetSpaceUser == null) {
                targetSpaceUser = spaceUserService.getById(spaceUserId);
            }
            if (targetSpaceUser == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间用户信息");
            }

            Space targetSpace = authContext.getSpace();
            if (targetSpace == null) {
                targetSpace = spaceService.getById(targetSpaceUser.getSpaceId());
            }
            if (targetSpace == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间信息");
            }
            // 私有空间没有成员体系，但历史数据中可能仍存在成员记录。
            // 此时应按空间本身鉴权，允许空间创建者和系统管理员处理，避免误用团队成员关系判断。
            if (targetSpace.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()) {
                return spaceUserAuthManager.getPermissionList(targetSpace, loginUser);
            }

            // 再查询当前登录用户在该空间的成员关系；权限必须取自操作者，而不是目标成员。
            // 只有已接受邀请的成员才算正式加入空间，待确认或已拒绝的记录不能获得权限。
            SpaceUser currentUserSpaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getUserId, userId)
                    .eq(SpaceUser::getSpaceId, targetSpaceUser.getSpaceId())
                    .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.ACCEPTED.getValue())
                    .one();
            if (currentUserSpaceUser == null) {
                return Collections.emptyList();
            }
            return spaceUserAuthManager.getPermissionsByRole(currentUserSpaceUser.getSpaceRole());
        }
        // 优先复用业务层已经查询出的 Picture / Space，只有上下文仅提供 id 时才回退查库。
        Picture picture = authContext.getPicture();
        Space space = authContext.getSpace();
        Long spaceId = authContext.getSpaceId();
        if (space != null) {
            spaceId = space.getId();
        }
        if (picture == null && spaceId == null) {
            Long pictureId = authContext.getPictureId();
            if (pictureId != null) {
                picture = pictureService.lambdaQuery()
                        .eq(Picture::getId, pictureId)
                        .select(Picture::getId, Picture::getSpaceId, Picture::getUserId)
                        .one();
            }
        }
        if (picture != null) {
            spaceId = picture.getSpaceId();
            // 公共图库，仅本人和管理员可以操作
            if (spaceId == null) {
                if (picture.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                    return adminPermissions;
                }
                // 只有浏览权限
                return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
            }
        }

        if (spaceId == null) {
            return Collections.emptyList();
        }
        if (space == null || !spaceId.equals(space.getId())) {
            space = spaceService.getById(spaceId);
        }
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间信息");
        }
        // 私有空间
        if (space.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()) {
            // 仅本人或管理员可以操作
            if (space.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                return adminPermissions;
            }
            // 无权限
            return Collections.emptyList();
        } else {
            // 团队空间
            // 查询当前登录用户在该空间的角色
            SpaceUser spaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.ACCEPTED.getValue())
                    .one();
            if (spaceUser == null) {
                return Collections.emptyList();
            }
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验) 本项目不使用
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    /**
     * 获取业务代码在当前线程中设置的鉴权上下文。
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        return SaTokenContextHolder.getContext();
    }
}
