package com.my.picturesystembackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.constant.CommonConstant;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.mapper.TagMapper;
import com.my.picturesystembackend.model.dto.tag.TagAddRequest;
import com.my.picturesystembackend.model.dto.tag.TagQueryRequest;
import com.my.picturesystembackend.model.dto.tag.TagUpdateRequest;
import com.my.picturesystembackend.model.entity.Tag;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.TagVO;
import com.my.picturesystembackend.service.TagService;
import com.my.picturesystembackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author helloworld
* @description 针对表【tag(图片标签)】的数据库操作Service实现
* @createDate 2025-09-27 23:45:05
*/
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    private final UserService userService;

    @Override
    public Long addTag(TagAddRequest tagAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(tagAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Tag tag = BeanUtil.toBean(tagAddRequest, Tag.class);
        tag.setUserId(loginUser.getId());
        isExists(tag);
        boolean save = this.save(tag);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加标签失败");
        }
        return tag.getId();
    }

    @Override
    public TagVO getTagVOById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Tag tag = this.getById(id);
        if (tag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        TagVO tagVO = BeanUtil.toBean(tag, TagVO.class);
        Long userId = tag.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            tagVO.setUserVO(userService.getUserVO(user));
        }
        return tagVO;
    }

    @Override
    public boolean deleteTag(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 判断是否存在
        Tag oldTag = this.getById(id);
        if (oldTag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = this.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除标签失败");
        }
        return true;
    }

    @Override
    public boolean updateTag(TagUpdateRequest tagUpdateRequest) {
        ThrowUtils.throwIf(tagUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = tagUpdateRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Tag oldTag = this.getById(id);
        ThrowUtils.throwIf(oldTag == null, ErrorCode.NOT_FOUND_ERROR);
        Tag newTag = BeanUtil.toBean(tagUpdateRequest, Tag.class);
        boolean update = this.updateById(newTag);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR, "更新标签失败");
        return true;
    }

    @Override
    public QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        if (tagQueryRequest == null) {
            return queryWrapper;
        }
        Long id = tagQueryRequest.getId();
        String name = tagQueryRequest.getName();
        Long userId = tagQueryRequest.getUserId();
        String sortField = tagQueryRequest.getSortField();
        String sortOrder = tagQueryRequest.getSortOrder();

        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);

        // 排序
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<TagVO> getTagVOPage(TagQueryRequest tagQueryRequest) {
        Page<Tag> tagPage = this.page(new Page<>(tagQueryRequest.getCurrent(), tagQueryRequest.getPageSize()),
                this.getQueryWrapper(tagQueryRequest));
        Page<TagVO> tagVOPage = new Page<>(tagPage.getCurrent(), tagPage.getSize(), tagPage.getTotal());
        List<Tag> tagList = tagPage.getRecords();
        if (CollUtil.isEmpty(tagList)) {
            return tagVOPage;
        }
        Set<Long> userIdSet = tagList.stream().map(Tag::getUserId).collect(Collectors.toSet());
        Map<Long, User> userIdToMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        List<TagVO> tagVOList = tagList.stream()
                .map(tag -> {
                    TagVO tagVO = BeanUtil.toBean(tag, TagVO.class);
                    tagVO.setUserVO(userService.getUserVO(userIdToMap.get(tag.getUserId())));
                    return tagVO;
                })
                .collect(Collectors.toList());
        tagVOPage.setRecords(tagVOList);
        return tagVOPage;
    }

    @Override
    public TagVO getTagVO(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagVO tagVO = BeanUtil.toBean(tag, TagVO.class);
        Long userId = tag.getUserId();
        if (userId != null && userId > 0L) {
            User user = userService.getById(userId);
            tagVO.setUserVO(userService.getUserVO(user));
        }
        return tagVO;
    }

    @Override
    public List<TagVO> getTagVOList(List<Tag> tagList) {
        if (CollUtil.isEmpty(tagList)) {
            return new ArrayList<>();
        }
        Set<Long> userIdSet = tagList.stream()
                .map(Tag::getUserId)
                .collect(Collectors.toSet());
        Map<Long, User> userIdToUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        return tagList.stream()
                .map(tag -> {
                    TagVO tagVO = BeanUtil.toBean(tag, TagVO.class);
                    Long userId = tag.getUserId();
                    if (userId != null && userId > 0L) {
                        User user = userIdToUserMap.get(userId);
                        tagVO.setUserVO(userService.getUserVO(user));
                    }
                    return tagVO;
                })
                .collect(Collectors.toList());
    }

    private void isExists(Tag tag) {
        boolean exists = this.lambdaQuery()
                .eq(Tag::getName, tag.getName())
                .exists();
        if (exists) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名称已存在");
        }
    }
}




