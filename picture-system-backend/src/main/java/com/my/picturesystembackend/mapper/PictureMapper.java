package com.my.picturesystembackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.my.picturesystembackend.model.dto.picture.PictureQueryRequest;
import com.my.picturesystembackend.model.entity.Picture;
import com.my.picturesystembackend.model.vo.PictureAdminVO;
import com.my.picturesystembackend.model.vo.PictureVO;
import org.apache.ibatis.annotations.Param;

/**
* @author helloworld
* @description 针对表【picture(图片)】的数据库操作Mapper
* @createDate 2025-10-11 19:19:46
* @Entity com.my.picturesystembackend.model.entity.Picture
*/
public interface PictureMapper extends BaseMapper<Picture> {

    /**
     * 根据id获取图片管理员VO
     * @param id id
     * @return 图片管理员VO
     */
    PictureAdminVO getPictureAdminVO(@Param("id") Long id);

    /**
     * 分页获取pictureAdminVO
     *
     * @param page 分页
     * @param pictureQueryRequest 查询条件
     * @return 分页列表
     */
    Page<PictureAdminVO> listPictureAdminVOByPage(@Param("page") Page<PictureAdminVO> page,
                                                  @Param("req") PictureQueryRequest pictureQueryRequest);

    /**
     * 分页获取pictureVO
     *
     * @param page 分页
     * @param pictureQueryRequest 搜索条件
     * @return 分页列表
     */
    Page<PictureVO> listPictureVOByPage(@Param("page") Page<PictureVO> page,
                                        @Param("req") PictureQueryRequest pictureQueryRequest);
}




