package com.my.picturesystembackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.picturesystembackend.annotation.AuthCheck;
import com.my.picturesystembackend.common.BaseResponse;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.common.ResultUtils;
import com.my.picturesystembackend.constant.UserConstant;
import com.my.picturesystembackend.exception.BusinessException;
import com.my.picturesystembackend.exception.ErrorCode;
import com.my.picturesystembackend.exception.ThrowUtils;
import com.my.picturesystembackend.model.dto.category.CategoryAddRequest;
import com.my.picturesystembackend.model.dto.category.CategoryQueryRequest;
import com.my.picturesystembackend.model.dto.category.CategoryUpdateRequest;
import com.my.picturesystembackend.model.entity.Category;
import com.my.picturesystembackend.model.vo.CategoryVO;
import com.my.picturesystembackend.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 添加图片分类
     * @param categoryAddRequest 添加图片分类请求体
     * @param request request
     * @return 图片分类id
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加图片分类", notes = "添加图片分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCategory(@RequestBody CategoryAddRequest categoryAddRequest, HttpServletRequest request) {
        return ResultUtils.success(categoryService.addCategory(categoryAddRequest, request));
    }

    /**
     * 删除图片分类
     * @param deleteRequest 删除请求体
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除图片分类", notes = "删除图片分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCategory(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(categoryService.deleteCategory(deleteRequest));
    }

    /**
     * 更新图片分类
     * @param categoryUpdateRequest 更新图片分类请求体
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新图片分类", notes = "更新图片分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCategory(@RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        return ResultUtils.success(categoryService.updateCategory(categoryUpdateRequest));
    }

    /**
     * 获取图片分类（仅管理员）
     * @param id 图片分类 id
     * @return 图片分类
     */
    @GetMapping("/get")
    @ApiOperation(value = "获取图片分类", notes = "获取图片分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Category> getCategoryById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0L, ErrorCode.PARAMS_ERROR);
        Category category = categoryService.getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片分类不存在");
        }
        return ResultUtils.success(category);
    }

    /**
     * 获取图片分类VO
     * @param id 分类id
     * @return 图片分类VO
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "获取图片分类VO", notes = "根据id获取图片分类VO")
    public BaseResponse<CategoryVO> getCategoryVOById(Long id) {
        return ResultUtils.success(categoryService.getCategoryVOById(id));
    }

    /**
     * 分页获取图片分类
     * @param categoryQueryRequest 图片分类查询请求体
     * @return 图片分类列表
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取图片分类", notes = "分页获取图片分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Category>> listCategoryByPage(@RequestBody CategoryQueryRequest categoryQueryRequest) {
        long current = categoryQueryRequest.getCurrent();
        long size = categoryQueryRequest.getPageSize();

        Page<Category> categoryPage = categoryService.page(new Page<>(current, size),
                categoryService.getQueryWrapper(categoryQueryRequest));
        return ResultUtils.success(categoryPage);
    }

    /**
     * 分页获取图片分类VO
     * @param categoryQueryRequest 图片分类请求体
     * @return 图片分类VO列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取图片分类VO", notes = "分页获取图片分类VO")
    public BaseResponse<Page<CategoryVO>> listCategoryVOByPage(@RequestBody CategoryQueryRequest categoryQueryRequest) {
        return ResultUtils.success(categoryService.getCategoryVOPage(categoryQueryRequest));
    }

    /**
     * 获取所有图片分类VO（前端创建图片时使用）
     * @return CategoryVOList
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取图片分类列表", notes = "获取图片分类列表")
    public BaseResponse<List<CategoryVO>> listCategoryVO() {
        List<Category> list = categoryService.list();
        return ResultUtils.success(categoryService.getCategoryVOList(list));
    }
}
