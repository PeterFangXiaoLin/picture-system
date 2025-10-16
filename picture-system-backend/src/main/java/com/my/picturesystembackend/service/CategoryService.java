package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.model.dto.category.CategoryAddRequest;
import com.my.picturesystembackend.model.dto.category.CategoryQueryRequest;
import com.my.picturesystembackend.model.dto.category.CategoryUpdateRequest;
import com.my.picturesystembackend.model.entity.Category;
import com.my.picturesystembackend.model.vo.CategoryVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author helloworld
* @description 针对表【category(图片分类)】的数据库操作Service
* @createDate 2025-10-02 11:09:19
*/
public interface CategoryService extends IService<Category> {

    /**
     * 添加图片分类
     * @param categoryAddRequest 添加图片分类请求体
     * @param request 请求
     * @return 图片分类id
     */
    Long addCategory(CategoryAddRequest categoryAddRequest, HttpServletRequest request);

    /**
     * 删除图片分类
     * @param deleteRequest 删除请求体
     * @return 是否删除成功
     */
    boolean deleteCategory(DeleteRequest deleteRequest);

    /**
     * 修改图片分类
     * @param categoryUpdateRequest 修改图片分类请求体
     * @return 是否修改成功
     */
    boolean updateCategory(CategoryUpdateRequest categoryUpdateRequest);

    /**
     * 根据 id 获取图片分类VO
     * @param id 图片分类 id
     * @return 图片分类VO
     */
    CategoryVO getCategoryVOById(Long id);

    /**
     * 获取图片分类分页
     * @param categoryQueryRequest 图片分类查询请求体
     * @return 图片分类分页
     */
    QueryWrapper<Category> getQueryWrapper(CategoryQueryRequest categoryQueryRequest);

    /**
     * 获取图片分类分页
     * @param categoryQueryRequest 图片分类查询请求体
     * @return 图片分类分页
     */
    Page<CategoryVO> getCategoryVOPage(CategoryQueryRequest categoryQueryRequest);

    /**
     * 获取图片分类VO
     * @param category 图片分类
     * @return 图片分类VO
     */
    CategoryVO getCategoryVO(Category category);

    /**
     * 获取图片分类VO列表
     * @param categoryList 图片分类列表
     * @return 图片分类VO列表
     */
    List<CategoryVO> getCategoryVOList(List<Category> categoryList);
}
