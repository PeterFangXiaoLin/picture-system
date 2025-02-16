package com.my.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.common.DeleteRequest;
import com.my.common.ErrorCode;
import com.my.constant.CommonConstant;
import com.my.constant.UserConstant;
import com.my.controller.vo.picture.*;
import com.my.controller.vo.user.UserRespVO;
import com.my.domain.dto.file.UploadPictureResult;
import com.my.domain.entity.Picture;
import com.my.domain.entity.User;
import com.my.exception.BusinessException;
import com.my.exception.ThrowUtils;
import com.my.manager.FileManager;
import com.my.mapper.PictureMapper;
import com.my.service.PictureService;
import com.my.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.my.constant.UserConstant.ADMIN_ROLE;

/**
* @author helloworld
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-02-15 11:52:27
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    @Override
    public PictureRespVO uploadPicture(MultipartFile multipartFile, PictureUploadReqVO pictureUploadReqVO, HttpServletRequest request) {
        UserRespVO userRespVO = (UserRespVO) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjUtil.isNull(userRespVO), ErrorCode.NO_AUTH_ERROR);
        Long pictureId = null;
        if (ObjUtil.isNotNull(pictureUploadReqVO)) {
            pictureId = pictureUploadReqVO.getId();
        }
        // 校验图片是否存在
        if (ObjUtil.isNotNull(pictureId)) {
            validPictureExists(pictureId);
        }

        // 上传图片，得到图片信息
        // 按照用户 id 划分目录
        String uploadFilePrefix = String.format("public/%s", userRespVO.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPictureByStream(    multipartFile, uploadFilePrefix);
        // 构造图片信息保存到数据库
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(userRespVO.getId());
        // 如果pictureId不为空，则是更新
        if (ObjUtil.isNotNull(pictureId)) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }

        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "图片上传失败");
        return PictureRespVO.doToVo(picture);
    }

    @Override
    public PictureRespVO getPictureRespVO(Picture picture) {
        // 数据库对象转 VO
        PictureRespVO pictureRespVO = PictureRespVO.doToVo(picture);
        // 设置用户信息
        Long userId = picture.getUserId();
        if (ObjUtil.isNotNull(userId) && userId > 0) {
            pictureRespVO.setUserRespVO(userService.getUserVO(userId));
        }
        return pictureRespVO;
    }

    @Override
    public Page<PictureRespVO> getPictureRespVOPage(Page<Picture> picturePage) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureRespVO> pictureRespVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureRespVOPage;
        }

        // 对象列表 => 封装对象列表
        List<PictureRespVO> pictureRespVOList = pictureList.stream().map(PictureRespVO::doToVo).collect(Collectors.toList());
        // 关联用户信息
        // 根据用户id查询一次，而不是循环调库
        Set<Long> userIds = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userMap = userService.listByIds(userIds).stream().collect(Collectors.groupingBy(User::getId));
        pictureRespVOList.forEach(pictureRespVO -> {
            Long userId = pictureRespVO.getUserId();
            User user = null;
            if (userMap.containsKey(userId)) {
                user = userMap.get(userId).get(0);
            }
            if (ObjUtil.isNotNull(user)) {
                pictureRespVO.setUserRespVO(BeanUtil.toBean(user, UserRespVO.class));
            }
        });
        return pictureRespVOPage;
    }

    @Override
    public Boolean deletePicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isNull(deleteRequest), ErrorCode.PARAMS_ERROR);
        Long pictureId = deleteRequest.getId();
        // 校验是否存在
        ThrowUtils.throwIf(ObjUtil.isNull(pictureId), ErrorCode.PARAMS_ERROR);
        Picture picture = pictureMapper.selectById(pictureId);
        ThrowUtils.throwIf(ObjUtil.isNull(picture), ErrorCode.PARAMS_ERROR, "图片不存在");
        // 仅管理员和自己创建的照片才能删除
        UserRespVO loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(picture.getUserId()) && !ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = this.removeById(pictureId);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除失败");
        return true;
    }

    @Override
    public Boolean updatePicture(PictureUpdateReqVO pictureUpdateReqVO) {
        ThrowUtils.throwIf(ObjUtil.isNull(pictureUpdateReqVO), ErrorCode.PARAMS_ERROR);
        // 校验是否存在
        validPictureExists(pictureUpdateReqVO.getId());
        // dto 转 do
        Picture picture = BeanUtil.toBean(pictureUpdateReqVO, Picture.class);
        // 将List 标签 转为 json array 标签
        List<String> tagList = pictureUpdateReqVO.getTags();
        if (CollUtil.isNotEmpty(tagList)) {
            ThrowUtils.throwIf(tagList.size() > 7, ErrorCode.PARAMS_ERROR, "标签不能超过7个");
            picture.setTags(JSONUtil.toJsonStr(pictureUpdateReqVO.getTags()));
        }
        // 校验参数是否合法
        validPicture(picture);
        // 更新数据库
        boolean result = this.updateById(picture);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return true;
    }

    @Override
    public Picture getPictureById(Long id) {
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR);
        Picture picture = pictureMapper.selectById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(picture), ErrorCode.PARAMS_ERROR, "图片不存在");
        return picture;
    }

    @Override
    public PictureRespVO getPictureVOById(Long id) {
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR);
        Picture picture = pictureMapper.selectById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(picture), ErrorCode.PARAMS_ERROR, "图片不存在");
        return getPictureRespVO(picture);
    }

    @Override
    public Page<Picture> listPictureByPage(PictureQueryReqVO pictureQueryReqVO) {
        QueryWrapper<Picture> queryWrapper = getQueryWrapper(pictureQueryReqVO);
        return pictureMapper.selectPage(new Page<>(pictureQueryReqVO.getCurrent(), pictureQueryReqVO.getPageSize()), queryWrapper);
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryReqVO pictureQueryReqVO) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryReqVO == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryReqVO.getId();
        String name = pictureQueryReqVO.getName();
        String introduction = pictureQueryReqVO.getIntroduction();
        String category = pictureQueryReqVO.getCategory();
        List<String> tags = pictureQueryReqVO.getTags();
        Long picSize = pictureQueryReqVO.getPicSize();
        Integer picWidth = pictureQueryReqVO.getPicWidth();
        Integer picHeight = pictureQueryReqVO.getPicHeight();
        Double picScale = pictureQueryReqVO.getPicScale();
        String picFormat = pictureQueryReqVO.getPicFormat();
        String searchText = pictureQueryReqVO.getSearchText();
        Long userId = pictureQueryReqVO.getUserId();
        String sortField = pictureQueryReqVO.getSortField();
        String sortOrder = pictureQueryReqVO.getSortOrder();

        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or().like("introduction", searchText)
            );
        }

        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotNull(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotNull(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotNull(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotNull(picScale), "picScale", picScale);

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<PictureRespVO> listPictureRespVOByPage(PictureQueryReqVO pictureQueryReqVO) {
        // 限制爬虫
        ThrowUtils.throwIf(pictureQueryReqVO.getPageSize() > 20, ErrorCode.PARAMS_ERROR);
        Page<Picture> picturePage = listPictureByPage(pictureQueryReqVO);
        return getPictureRespVOPage(picturePage);
    }

    @Override
    public Boolean editPicture(PictureEditReqVO pictureEditReqVO, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isNull(pictureEditReqVO), ErrorCode.PARAMS_ERROR);
        // 校验是否存在
        Picture oldPicture = pictureMapper.selectById(pictureEditReqVO.getId());
        ThrowUtils.throwIf(ObjUtil.isNull(oldPicture), ErrorCode.PARAMS_ERROR, "图片不存在");

        // 判断是否登录
        UserRespVO loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NO_AUTH_ERROR, "未登录");

        Picture picture = BeanUtil.toBean(pictureEditReqVO, Picture.class);

        // 校验参数是否合法
        validPicture(picture);

        List<String> tags = pictureEditReqVO.getTags();
        if (CollUtil.isNotEmpty(tags)) {
            ThrowUtils.throwIf(tags.size() > 7, ErrorCode.PARAMS_ERROR, "标签不能超过7个");
            picture.setTags(JSONUtil.toJsonStr(tags));
        }

        // 检验权限
        if (!loginUser.getUserRole().equals(ADMIN_ROLE) && !loginUser.getId().equals(picture.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        int result = pictureMapper.updateById(picture);
        ThrowUtils.throwIf(result <= 0, ErrorCode.SYSTEM_ERROR, "修改失败");
        return true;
    }

    private void validPictureExists(Long pictureId) {
        ThrowUtils.throwIf(ObjUtil.isNull(pictureId), ErrorCode.PARAMS_ERROR);
        Picture picture = pictureMapper.selectById(pictureId);
        ThrowUtils.throwIf(ObjUtil.isNull(picture), ErrorCode.PARAMS_ERROR, "图片不存在");
    }
    private void validPicture(Picture picture) {
        ThrowUtils.throwIf(ObjUtil.isNull(picture), ErrorCode.PARAMS_ERROR);
        // 校验参数是否合法
        Long id = picture.getId();
        String url = picture.getUrl();
        String name = picture.getName();
        String introduction = picture.getIntroduction();
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR);
        // 直接通过url修改照片
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(name)) {
            ThrowUtils.throwIf(name.length() > 50, ErrorCode.PARAMS_ERROR, "图片名字太长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介太长");
        }
    }
}




