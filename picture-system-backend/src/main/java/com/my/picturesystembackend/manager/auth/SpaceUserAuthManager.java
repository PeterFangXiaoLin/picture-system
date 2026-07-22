package com.my.picturesystembackend.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.my.picturesystembackend.manager.auth.model.SpaceUserAuthConfig;
import com.my.picturesystembackend.manager.auth.model.SpaceUserRole;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.SpaceUser;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.enums.SpaceRoleEnum;
import com.my.picturesystembackend.model.enums.SpaceTypeEnum;
import com.my.picturesystembackend.model.enums.SpaceUserInviteStatusEnum;
import com.my.picturesystembackend.service.SpaceUserService;
import com.my.picturesystembackend.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 加载权限配置文件
 */
@Component
public class SpaceUserAuthManager {

    @Resource
    private UserService userService;

    @Resource
    private SpaceUserService spaceUserService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据 spaceUserRole 返回 该角色拥有的权限符列表
     *
     * @param spaceUserRole 空间用户角色
     * @return 权限列表
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (StrUtil.isBlank(spaceUserRole)) {
            return Collections.emptyList();
        }
        // 找到匹配的角色
        SpaceUserRole userRole = SPACE_USER_AUTH_CONFIG.getRoles().stream()
                .filter(role -> role.getKey().equals(spaceUserRole))
                .findFirst()
                .orElse(null);
        return userRole == null ? Collections.emptyList() : userRole.getPermissions();
    }

    /**
     * 使用业务代码已经查询出的对象进行编程式鉴权。
     *
     * <p>上下文只在本次校验期间可见，并在 finally 中清理，避免线程池复用导致数据串请求。</p>
     *
     * @param permission 需要的权限码
     * @param authContext 被操作对象上下文
     */
    public void checkPermission(String permission, SpaceUserAuthContext authContext) {
        SaTokenContextHolder.setContext(authContext);
        try {
            StpKit.SPACE.checkPermission(permission);
        } finally {
            SaTokenContextHolder.clear();
        }
    }

    /**
     * 获取指定业务上下文下的完整权限列表，供既要校验又要返回权限列表的场景复用。
     */
    public List<String> getPermissionList(SpaceUserAuthContext authContext) {
        SaTokenContextHolder.setContext(authContext);
        try {
            return StpKit.SPACE.getPermissionList();
        } finally {
            SaTokenContextHolder.clear();
        }
    }

    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return Collections.emptyList();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            }
            return Collections.emptyList();
        }

        // 非公共图库，获取空间类型
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return Collections.emptyList();
        }
        switch (spaceTypeEnum) {
            case PRIVATE:
                if (userService.isAdmin(loginUser) || space.getUserId().equals(loginUser.getId())) {
                    return ADMIN_PERMISSIONS;
                }
                return Collections.emptyList();
            case TEAM:
                // 团队空间
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .eq(SpaceUser::getInviteStatus, SpaceUserInviteStatusEnum.ACCEPTED.getValue())
                        .one();
                if (spaceUser == null) {
                    return Collections.emptyList();
                }
                return getPermissionsByRole(spaceUser.getSpaceRole());
        }
        return Collections.emptyList();
    }
}
