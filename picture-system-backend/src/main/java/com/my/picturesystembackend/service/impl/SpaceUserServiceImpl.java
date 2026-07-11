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
    public Long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);
        // 查询数据库是否存在记录
        boolean exists = this.lambdaQuery()
                .eq(SpaceUser::getSpaceId, spaceUser.getSpaceId())
                .eq(SpaceUser::getUserId, spaceUser.getUserId())
                .exists();
        if (exists) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已在空间中");
        }

        // 操作数据库
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
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
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotNull(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        queryWrapper.eq(StrUtil.isNotBlank(spaceRole), "spaceRole", spaceRole);
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




