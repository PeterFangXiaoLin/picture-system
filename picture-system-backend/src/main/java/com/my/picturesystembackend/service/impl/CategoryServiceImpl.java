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
import com.my.picturesystembackend.mapper.CategoryMapper;
import com.my.picturesystembackend.model.dto.category.CategoryAddRequest;
import com.my.picturesystembackend.model.dto.category.CategoryQueryRequest;
import com.my.picturesystembackend.model.dto.category.CategoryUpdateRequest;
import com.my.picturesystembackend.model.entity.Category;
import com.my.picturesystembackend.model.entity.User;
import com.my.picturesystembackend.model.vo.CategoryVO;
import com.my.picturesystembackend.service.CategoryService;
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
* @description 针对表【category(图片分类)】的数据库操作Service实现
* @createDate 2025-10-02 11:09:19
*/
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    private final UserService userService;

    @Override
    public Long addCategory(CategoryAddRequest categoryAddRequest, HttpServletRequest request) {
        Category category = BeanUtil.toBean(categoryAddRequest, Category.class);
        // 校验参数
        validCategory(category, true);

        User loginUser = userService.getLoginUser(request);
        category.setUserId(loginUser.getId());

        // 校验名称是否存在
        isExists(category);

        // 插入数据库
        boolean save = this.save(category);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加分类失败");
        }
        return category.getId();
    }

    @Override
    public boolean deleteCategory(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0L, ErrorCode.PARAMS_ERROR);
        Category oldCategory = this.getById(id);
        if (oldCategory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }
        // 操作数据库
        boolean result = this.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除分类失败");
        }
        return true;
    }

    @Override
    public boolean updateCategory(CategoryUpdateRequest categoryUpdateRequest) {
        Category category = BeanUtil.toBean(categoryUpdateRequest, Category.class);
        // 校验参数
        validCategory(category, false);

        // 判断分类是否存在
        Long id = categoryUpdateRequest.getId();
        Category oldCategory = this.getById(id);
        if (oldCategory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 校验分类名称是否存在
        isExists(category);

        // 操作数据库
        boolean result = this.updateById(category);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新分类失败");
        }
        return true;
    }

    @Override
    public CategoryVO getCategoryVOById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }
        return this.getCategoryVO(category);
    }

    @Override
    public QueryWrapper<Category> getQueryWrapper(CategoryQueryRequest categoryQueryRequest) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        if (categoryQueryRequest == null) {
            return queryWrapper;
        }

        Long id = categoryQueryRequest.getId();
        String name = categoryQueryRequest.getName();
        Long userId = categoryQueryRequest.getUserId();
        String sortField = categoryQueryRequest.getSortField();
        String sortOrder = categoryQueryRequest.getSortOrder();

        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);

        // 排序
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<CategoryVO> getCategoryVOPage(CategoryQueryRequest categoryQueryRequest) {
        Page<Category> categoryPage = this.page(new Page<>(categoryQueryRequest.getCurrent(), categoryQueryRequest.getPageSize()),
                this.getQueryWrapper(categoryQueryRequest));
        Page<CategoryVO> categoryVOPage = new Page<>(categoryPage.getCurrent(), categoryPage.getSize(), categoryPage.getTotal());

        List<Category> categoryList = categoryPage.getRecords();
        if (CollUtil.isEmpty(categoryList)) {
            return categoryVOPage;
        }

        Set<Long> userIdSet = categoryList.stream()
                .map(Category::getUserId)
                .collect(Collectors.toSet());
        Map<Long, User> userIdToUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<CategoryVO> categoryVOList = categoryList.stream()
                .map(category -> {
                    CategoryVO categoryVO = BeanUtil.toBean(category, CategoryVO.class);
                    categoryVO.setUserVO(userService.getUserVO(userIdToUserMap.get(category.getUserId())));
                    return categoryVO;
                })
                .collect(Collectors.toList());
        categoryVOPage.setRecords(categoryVOList);
        return categoryVOPage;
    }

    @Override
    public CategoryVO getCategoryVO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryVO categoryVO = BeanUtil.toBean(category, CategoryVO.class);
        Long userId = category.getUserId();
        if (userId != null && userId > 0L) {
            User user = userService.getById(userId);
            categoryVO.setUserVO(userService.getUserVO(user));
        }
        return categoryVO;
    }

    @Override
    public List<CategoryVO> getCategoryVOList(List<Category> categoryList) {
        if (CollUtil.isEmpty(categoryList)) {
            return new ArrayList<>();
        }
        Set<Long> userIdSet = categoryList.stream()
                .map(Category::getUserId)
                .collect(Collectors.toSet());
        Map<Long, User> userIdToUserMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        return categoryList.stream()
                .map(category -> {
                    CategoryVO categoryVO = BeanUtil.toBean(category, CategoryVO.class);
                    Long userId = category.getUserId();
                    if (userId != null && userId > 0L) {
                        User user = userIdToUserMap.get(userId);
                        categoryVO.setUserVO(userService.getUserVO(user));
                    }
                    return categoryVO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 校验参数
     *
     * @param category 分类
     * @param isAdd 是否为添加
     */
    private void validCategory(Category category, boolean isAdd) {
        ThrowUtils.throwIf(category == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(category.getName()), ErrorCode.PARAMS_ERROR, "分类名称不能为空");
        if (!isAdd) {
            ThrowUtils.throwIf(category.getId() == null || category.getId() <= 0L, ErrorCode.PARAMS_ERROR, "分类id不能为空");
        }
    }

    /**
     * 判断分类是否存在
     *
     * @param category 分类
     */
    private void isExists(Category category) {
        boolean exists = this.lambdaQuery()
                .ne(ObjUtil.isNotNull(category.getId()), Category::getId, category.getId())
                .eq(Category::getName, category.getName())
                .exists();
        if (exists) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称已存在");
        }
    }
}




