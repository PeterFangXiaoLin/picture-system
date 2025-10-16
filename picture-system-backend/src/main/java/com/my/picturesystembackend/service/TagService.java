package com.my.picturesystembackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.picturesystembackend.common.DeleteRequest;
import com.my.picturesystembackend.model.dto.tag.TagAddRequest;
import com.my.picturesystembackend.model.dto.tag.TagQueryRequest;
import com.my.picturesystembackend.model.dto.tag.TagUpdateRequest;
import com.my.picturesystembackend.model.entity.Tag;
import com.my.picturesystembackend.model.vo.TagVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author helloworld
* @description 针对表【tag(图片标签)】的数据库操作Service
* @createDate 2025-09-27 23:45:05
*/
public interface TagService extends IService<Tag> {

    /**
     * 添加标签
     * @param tagAddRequest 添加标签请求体
     * @param request request
     * @return 新标签 id
     */
    Long addTag(TagAddRequest tagAddRequest, HttpServletRequest request);

    /**
     * 根据 id 获取标签VO
     * @param id 标签 id
     * @return 标签VO
     */
    TagVO getTagVOById(Long id);

    /**
     * 删除标签
     * @param deleteRequest 删除请求体
     * @return 是否删除成功
     */
    boolean deleteTag(DeleteRequest deleteRequest);

    /**
     * 修改标签
     * @param tagUpdateRequest 修改标签请求体
     * @return 是否修改成功
     */
    boolean updateTag(TagUpdateRequest tagUpdateRequest);

    /**
     * 获取查询包装类
     * @param tagQueryRequest 查询条件
     * @return 查询包装类
     */
    QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest);

    /**
     * 获取标签分页
     * @param tagQueryRequest 查询条件
     * @return 标签分页
     */
    Page<TagVO> getTagVOPage(TagQueryRequest tagQueryRequest);

    /**
     * 获取标签VO
     * @param tag 标签
     * @return 标签VO
     */
    TagVO getTagVO(Tag tag);

    /**
     * 获取标签VO列表
     * @param tagList 标签列表
     * @return 标签VO列表
     */
    List<TagVO> getTagVOList(List<Tag> tagList);
}
