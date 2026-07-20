package com.my.picturesystembackend.manager.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    @Value("${server.servlet.context-path}")
    private String contextPath;

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
        // 获取管理员权限列表
        List<String> ADMIN_PERMISSIONS = spaceUserAuthManager.getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 获取上下文对象
        SpaceUserAuthContext authContext = getAuthContextByRequest();
        // 校验是否全字段为空，认为查询公共图库，返回管理员权限
        if (isAllFieldsNull(authContext)) {
            return ADMIN_PERMISSIONS;
        }
        // 获取当前登录用户id
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }
        Long userId = loginUser.getId();
        // 优先从上下文中获取 SpaceUser 对象
        SpaceUser spaceUser = authContext.getSpaceUser();
        if (spaceUser != null) {
            return spaceUserAuthManager.getPermissionsByRole(spaceUser.getSpaceRole());
        }
        // spaceUserId 是“被操作的成员关系 ID”，并不一定属于当前登录用户。
        // 先通过目标成员关系确定本次操作发生在哪个团队空间。
        Long spaceUserId = authContext.getSpaceUserId();
        if (spaceUserId != null) {
            SpaceUser targetSpaceUser = spaceUserService.getById(spaceUserId);
            if (targetSpaceUser == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间用户信息");
            }

            Space targetSpace = spaceService.getById(targetSpaceUser.getSpaceId());
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
        // 如果没有 spaceUserId, 尝试通过 spaceId 或 pictureId 获取space对象并处理
        Long spaceId = authContext.getSpaceId();
        if (spaceId == null) {
            // 获取图片id
            Long pictureId = authContext.getPictureId();
            if (pictureId == null) {
                // 图片id也是空（同时空间id为空，spaceUserId为空），操作对象不在范围内，放行返回 管理员权限
                return ADMIN_PERMISSIONS;
            }
            Picture picture = pictureService.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .select(Picture::getId, Picture::getSpaceId, Picture::getUserId)
                    .one();
            if (picture == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到图片信息");
            }
            spaceId = picture.getSpaceId();
            // 公共图库，仅本人和管理员可以操作
            if (spaceId == null) {
                if (picture.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                }
                // 只有浏览权限
                return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
            }
        }

        // 查询空间信息
        Space space = spaceService.getById(spaceId);
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到空间信息");
        }
        // 私有空间
        if (space.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()) {
            // 仅本人或管理员可以操作
            if (space.getUserId().equals(userId) || userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            }
            // 无权限
            return Collections.emptyList();
        } else {
            // 团队空间
            // 查询当前登录用户在该空间的角色
            spaceUser = spaceUserService.lambdaQuery()
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
     * 从请求中构造出上下文对象
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());
        SpaceUserAuthContext authRequest;
        // 区分post, get
        if (ContentType.JSON.getValue().equals(contentType)) {
            String body = ServletUtil.getBody(request); // 流只能读取一次
            authRequest = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            authRequest = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }
        // 根据请求路径，区分id的含义
        Long id = authRequest.getId();
        if (ObjUtil.isNotNull(id)) {
            // 获取请求的 URI /api/picture/xxx
            String requestURI = request.getRequestURI();
            // 去掉 /api/ =》picture/xxx
            String partURI = requestURI.replace(contextPath + "/", "");
            // 获取前缀，即第一个/前面的内容
            String moduleName = StrUtil.subBefore(partURI, "/", false);
            switch (moduleName) {
                case "picture":
                    authRequest.setPictureId(id);
                    break;
                case "space":
                    authRequest.setSpaceId(id);
                    break;
                case "spaceUser":
                    authRequest.setSpaceUserId(id);
                    break;
                default:
            }
        }

        return authRequest;
    }

    /**
     * 利用反射判断对象全字段是否为null
     *
     * @param object 对象
     * @return 结果
     */
    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true;
        }
        // 获取所有字段并判断是否所有字段都为空
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                // 获取字段值
                .map(field -> ReflectUtil.getFieldValue(object, field))
                // 检查是否所有字段值都为空
                .allMatch(ObjUtil::isEmpty);
    }
}
