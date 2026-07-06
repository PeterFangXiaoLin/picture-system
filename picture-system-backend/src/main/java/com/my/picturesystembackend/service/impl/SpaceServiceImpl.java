package com.my.picturesystembackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.mapper.PictureMapper;
import com.my.picturesystembackend.mapper.SpaceMapper;
import com.my.picturesystembackend.model.dto.space.SpaceAddRequest;
import com.my.picturesystembackend.model.dto.space.SpaceEditRequest;
import com.my.picturesystembackend.model.dto.space.SpaceQueryRequest;
import com.my.picturesystembackend.model.dto.space.SpaceUpdateRequest;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.entity.Space;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.enums.SpaceLevelEnum;
import com.my.picturesystembackend.model.vo.SpaceVO;
import com.my.picturesystembackend.model.vo.UserVO;
import com.my.picturesystembackend.service.AsyncService;
import com.my.picturesystembackend.service.SpaceService;
import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
* @author helloworld
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-12-04 00:18:30
*/
@Service
@RequiredArgsConstructor
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{

    private final UserService userService;

    private final PictureMapper pictureMapper;

    private final AsyncService asyncService;

    private final TransactionTemplate transactionTemplate;

    private static final Map<Long, Object> lockMap = new ConcurrentHashMap<>();

    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 校验名称和空间级别
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);

        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
        }
        // 添加和修改数据时，空间级别进行校验
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不合法");
        }
        // 添加和修改数据时，空间名称校验
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别填充空间信息
        // 为空时填充，保证灵活性
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    @Override
    public Long addSpace(SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        // 1. 填充参数默认值
        User loginUser = userService.getLoginUser(request);
        Space space = new Space();
        BeanUtil.copyProperties(spaceAddRequest, space);

        // 补充空间名称默认值
        if (StrUtil.isBlank(space.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        // 补充空间级别默认值
        if (space.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        // 根据空间级别填充空间信息
        this.fillSpaceBySpaceLevel(space);
        // 2. 校验参数
        this.validSpace(space, true);
        // 填充创建用户 id
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // 3. 校验权限
        if (SpaceLevelEnum.COMMON.getValue() != space.getSpaceLevel() &&
                !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可创建专业版及以上空间");
        }
        // 添加空间
        // 4. 控制同一用户只能创建一个私有空间
        // 先提交事务 再释放锁
        Object lock = lockMap.computeIfAbsent(userId, k -> new Object());
        synchronized (lock) {
            try {
                Long newSpaceId = transactionTemplate.execute(status -> {
                    boolean exists = this.lambdaQuery()
                            .eq(Space::getUserId, userId)
                            .exists();
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅可创建一个空间");
                    boolean result = this.save(space);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                    return space.getId();
                });
                // 处理返回结果
                return Optional.ofNullable(newSpaceId).orElse(-1L);
            } finally {
                lockMap.remove(userId);
            }
        }
    }

    @Override
    public Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest) {
        ThrowUtils.throwIf(spaceUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        // 校验空间是否存在
        Long id = spaceUpdateRequest.getId();
        Space oldSpace = this.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 更新空间
        Space space = new Space();
        BeanUtil.copyProperties(spaceUpdateRequest, space);
        // 校验参数
        this.validSpace(space, false);
        // 填充字段
        this.fillSpaceBySpaceLevel(space);
        boolean result = this.updateById(space);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return true;
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转vo
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询创建用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollectionUtils.isEmpty(spaceList)) {
            return spaceVOPage;
        }

        // 对象列表 => vo 列表
        List<SpaceVO> spaceVOList = spaceList.stream()
                .map(SpaceVO::objToVo)
                .collect(Collectors.toList());

        // 关联查询创建用户信息
        Set<Long> userIdSet = spaceList.stream()
                .map(Space::getUserId)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 填充用户信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            if (userId != null && userId > 0) {
                User user = userMap.get(userId);
                UserVO userVO = userService.getUserVO(user);
                spaceVO.setUser(userVO);
            }
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }

        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotNull(spaceLevel), "spaceLevel", spaceLevel);
        // 拼接排序条件
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public Boolean deleteSpace(DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        // 校验空间是否存在
        Space oldSpace = this.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅管理员或创建者可删除
        checkSpaceAuth(loginUser, oldSpace);

        // 删除空间，同时删除空间中的图片，以及异步清空对象存储中的图片
        List<Picture> pictureList = new ArrayList<>();
        transactionTemplate.execute(status -> {
            QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
            pictureQueryWrapper.eq("spaceId", id);
            pictureList.addAll(pictureMapper.selectList(pictureQueryWrapper));
            if (!CollectionUtils.isEmpty(pictureList)) {
                List<Long> pictureIdList = pictureList.stream()
                        .map(Picture::getId)
                        .collect(Collectors.toList());
                int deleteCount = pictureMapper.deleteByIds(pictureIdList);
                ThrowUtils.throwIf(deleteCount != pictureIdList.size(), ErrorCode.OPERATION_ERROR, "删除空间图片失败");
            }
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return true;
        });
        if (!CollectionUtils.isEmpty(pictureList)) {
            pictureList.forEach(asyncService::clearPictureFile);
        }
        return true;
    }

    @Override
    public Boolean editSpace(SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceEditRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long id = spaceEditRequest.getId();
        // 校验空间是否存在
        Space oldSpace = this.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅创建者或管理员可编辑
        checkSpaceAuth(loginUser, oldSpace);
        // 编辑空间
        Space space = new Space();
        BeanUtil.copyProperties(spaceEditRequest, space);
        // 校验参数
        this.validSpace(space, false);
        // 填充参数
        this.fillSpaceBySpaceLevel(space);
        boolean result = this.updateById(space);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return true;
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        // 仅管理员或创建者可删除
        if (!userService.isAdmin(loginUser) && !space.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }
}




